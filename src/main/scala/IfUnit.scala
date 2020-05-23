import chisel3._
import chisel3.util.Cat

class IfUnitIn(implicit val conf:CAHPConfig) extends Bundle {
  val romData = UInt(conf.romDataWidth.W)
  val jumpAddress = UInt(conf.instAddrWidth.W)
  val jump = Bool()

  override def cloneType: this.type = new IfUnitIn()(conf).asInstanceOf[this.type]
}

class IfUnitOut(implicit val conf:CAHPConfig) extends Bundle {
  val romAddr = UInt(conf.romAddrWidth.W)
  val inst = UInt(conf.instDataWidth.W)
  val instAddr = UInt(conf.instAddrWidth.W)
  val stole = Bool()

  override def cloneType: this.type = new IfUnitOut()(conf).asInstanceOf[this.type]
}

class IfUnitPort(implicit val conf:CAHPConfig) extends Bundle {
  val in = Input(new IfUnitIn)
  val out = Output(new IfUnitOut)

  val enable = Input(Bool())
}

object romCacheStateType {
  val NotLoaded = false.B
  val Loaded = true.B
}

class IfUnit(implicit val conf: CAHPConfig) extends Module {
  val io = IO(new IfUnitPort)

  val pc = RegInit(0.U(conf.instAddrWidth.W))
  val romCache = Reg(UInt(conf.romDataWidth.W))
  val romCacheState = RegInit(romCacheStateType.NotLoaded)

  val stole = Wire(Bool())
  val inst = Wire(UInt(24.W))
  inst := DontCare
  val isLong = inst(0) === 1.U


  when(io.enable){

    // ProgramCounter
    when(!stole) {
      when(io.in.jump) {
        pc := io.in.jumpAddress
      }.otherwise {
        when(isLong) {
          pc := pc + 3.U
        }.otherwise {
          pc := pc + 2.U
        }
      }
    }.otherwise{
      pc := pc
    }

    // RomCache
    // Flush cache when jump
    when(io.in.jump){
      romCacheState := romCacheStateType.NotLoaded
      romCache := romCache
    }.otherwise{
      when(romCacheState === romCacheStateType.NotLoaded){
        romCache := io.in.romData
        when(isLong){
          when(pc(1) === 1.U) {
            // Fail to fetch instruction
            romCacheState := romCacheStateType.Loaded
          }.elsewhen(pc(1, 0) === 1.U){
            // Success to fetch instruction, but cache is not loaded
            romCacheState := romCacheStateType.NotLoaded
          }.otherwise{
            // Success to fetch instruction, and cache is loaded
            romCacheState := romCacheStateType.Loaded
          }
        }.otherwise{
          when(pc(1,0) === 3.U){
            // Fail to fetch instruction
            romCacheState := romCacheStateType.Loaded
          }.elsewhen(pc(1,0) === 2.U){
            // Success to fetch instruction, but cache is not loaded
            romCacheState := romCacheStateType.NotLoaded
          }.otherwise{
            // Success to fetch instruction, and cache is loaded
            romCacheState := romCacheStateType.Loaded
          }
        }
      }.otherwise{
        when(isLong){
          when(pc(1,0) === 0.U){
            romCache := romCache
          }.otherwise{
            romCache := io.in.romData
          }
        }.otherwise{
          when(pc(1) === 0.U){
            romCache := romCache
          }.otherwise{
            romCache := io.in.romData
          }
        }
      }
    }
  }.otherwise{
    pc := pc
    romCache := romCache
    romCacheState := romCacheState
  }

  when(io.in.jump){
    stole := false.B
    io.out.romAddr := pc(8, 2)
  }.otherwise{
    when(romCacheState === romCacheStateType.NotLoaded){
      io.out.romAddr := pc(8, 2)
      inst := getInst(pc, io.in.romData, io.in.romData)
      when(isLong){
        when(pc(1) === 1.U) {
          // Fail to fetch instruction
          stole := true.B
        }.otherwise{
          // Success to fetch instruction
          stole := false.B
        }
      }.otherwise{
        when(pc(1,0) === 3.U){
          // Fail to fetch instruction
          stole := true.B
        }.otherwise{
          // Success to fetch instruction
          stole := false.B
        }
      }
    }.otherwise{
      io.out.romAddr := pc(8, 2) + 1.U
      inst := getInst(pc, io.in.romData, romCache)
      stole := false.B
    }
  }

  io.out.instAddr := pc
  io.out.stole := stole
  when(stole){
    io.out.inst := 0.U
  }.otherwise{
    io.out.inst := inst
  }

  def getInst(pc:UInt, upperBlock:UInt, lowerBlock:UInt):UInt = {
    val inst = Wire(UInt(24.W))
    when(pc(1,0) === 0.U){
      inst := Cat(lowerBlock(23, 16), lowerBlock(15, 8), lowerBlock(7, 0))
    }.elsewhen(pc(1,0) === 1.U){
      inst := Cat(lowerBlock(31, 24), lowerBlock(23, 16), lowerBlock(15, 8))
    }.elsewhen(pc(1,0) === 2.U){
      inst := Cat(upperBlock(7, 0), lowerBlock(31, 24), lowerBlock(23, 16))
    }.otherwise {
      inst := Cat(upperBlock(15, 8), upperBlock(7, 0), lowerBlock(31, 24))
    }
    inst
  }

  when(conf.debugIf.B){
    printf("\n[IF] instAddress:0x%x\n", io.out.instAddr)
    printf("[IF] inst:0x%x\n", io.out.inst)
    printf("[IF] jump:%d\n", io.in.jump)
    printf("[IF] JumpAddress:0x%x\n", io.in.jumpAddress)
  }
}
