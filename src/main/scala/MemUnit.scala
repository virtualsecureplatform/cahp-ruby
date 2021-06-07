import chisel3._
import chisel3.util.Cat

class MemUnitIn(implicit val conf:CAHPConfig) extends Bundle {
  val data = UInt(16.W)
  val byte = Bool()
  val signExt = Bool()
  val read = Bool()
  val write = Bool()

  override def cloneType: this.type = new MemUnitIn()(conf).asInstanceOf[this.type]
}

class MemUnitPort(implicit val conf:CAHPConfig) extends Bundle {
  val addr = Input(new ExUnitOut)
  val in = Input(new MemUnitIn)
  val wbIn = Input(new WbUnitIn)
  val ramPort = new RamPort
  val out = Output(new WbUnitIn)

  val enable = Input(Bool())
}

class MemUnit(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new MemUnitPort())

  val pExInReg = RegInit(0.U.asTypeOf(new ExUnitOut))
  val pMemReg = RegInit(0.U.asTypeOf(new MemUnitIn))
  val pWbReg = RegInit(0.U.asTypeOf(new WbUnitIn))

  val regWriteEnable = RegInit(false.B)
  val regWriteAddr = RegInit(0.U(conf.ramAddrWidth.W))
  val regWriteData = RegInit(0.U(conf.ramDataWidth.W))

  val readData = Wire(UInt(conf.ramDataWidth.W))

  when(io.enable){
    pMemReg := io.in
    pWbReg := io.wbIn
    pExInReg := io.addr
  }

  io.out := pWbReg
  io.ramPort.addr := pExInReg.res(conf.dataAddrWidth-1,1)
  io.ramPort.writeData := DontCare
  io.ramPort.writeEnable := false.B

  def sign_ext_8bit(v:UInt) : UInt = {
    val res = Wire(UInt(16.W))
    when(v(7,7) === 1.U){
      res := Cat(0xFF.U(8.W), v)
    }.otherwise{
      res := v
    }
    res
  }

  regWriteEnable := io.ramPort.writeEnable
  regWriteAddr := io.ramPort.addr
  regWriteData := io.ramPort.writeData

  when(regWriteEnable && (regWriteAddr === io.ramPort.addr)){
    readData := regWriteData
  }.otherwise{
    readData := io.ramPort.readData
  }

  when(pMemReg.read){
    when(pMemReg.byte) {
      when(pMemReg.signExt){
        when(pExInReg.res(0) === 0.U){
          io.out.regWrite.writeData := sign_ext_8bit(readData(7, 0))
        }.otherwise{
          io.out.regWrite.writeData := sign_ext_8bit(readData(15, 8))
        }
      }.otherwise{
        when(pExInReg.res(0) === 0.U){
          io.out.regWrite.writeData := Cat(0.U(8.W), readData(7, 0))
        }.otherwise{
          io.out.regWrite.writeData := Cat(0.U(8.W), readData(15, 8))
        }
      }
    }.otherwise{
      io.out.regWrite.writeData := readData
    }
  }.otherwise{
    io.out.regWrite.writeData := pExInReg.res
  }

  when(pMemReg.write){
    io.ramPort.writeEnable := true.B
    when(pMemReg.byte){
      when(pExInReg.res(0) === 0.U){
        io.ramPort.writeData := Cat(readData(15, 8), pMemReg.data(7, 0))
      }.otherwise{
        io.ramPort.writeData := Cat(pMemReg.data(7, 0), readData(7, 0))
      }
    }.otherwise{
      io.ramPort.writeData := pMemReg.data
    }
  }
}
