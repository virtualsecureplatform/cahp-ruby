import chisel3._

class CoreUnitPort(implicit val conf:CAHPConfig) extends Bundle {
  val romPort = new RomPort()
  val ramPort = new RamPort()

  val mainRegOut = Output(new MainRegisterOutPort)
  val finishFlag = Output(Bool())
  val load = Input(Bool())
  val testRegx8 = if(conf.test) Output(UInt(16.W)) else Output(UInt(0.W))
}

class CoreUnit(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new CoreUnitPort())

  val ifUnit = Module(new IfUnit)
  val idwbUnit = Module(new IdWbUnit)
  val exUnit = Module(new ExUnit)
  val memUnit = Module(new MemUnit)

  val finishFlagReg = RegInit(false.B)
  finishFlagReg := finishFlagReg
  when(memUnit.io.out.finishFlag) {
    finishFlagReg := memUnit.io.out.finishFlag
  }
  io.finishFlag := finishFlagReg | memUnit.io.out.finishFlag


  io.testRegx8 := idwbUnit.io.mainRegOut.x8
  io.romPort.addr := ifUnit.io.out.romAddr
  io.mainRegOut := idwbUnit.io.mainRegOut
  ifUnit.io.in.romData := io.romPort.data

  io.ramPort.addr := memUnit.io.ramPort.addr
  io.ramPort.writeData := memUnit.io.ramPort.writeData
  io.ramPort.writeEnable := memUnit.io.ramPort.writeEnable

  ifUnit.io.in.jump := exUnit.io.out.jump
  ifUnit.io.in.jumpAddress := exUnit.io.out.jumpAddress
  ifUnit.io.enable := !idwbUnit.io.stole&&(!io.load)

  idwbUnit.io.idIn := ifUnit.io.out
  idwbUnit.io.wbIn := memUnit.io.out
  idwbUnit.io.exRegWriteIn := exUnit.io.wbOut.regWrite
  idwbUnit.io.memRegWriteIn := memUnit.io.out.regWrite
  idwbUnit.io.exMemIn := exUnit.io.memOut
  idwbUnit.io.idFlush := exUnit.io.out.jump
  idwbUnit.io.idEnable := true.B&&(!io.load)

  exUnit.io.in := idwbUnit.io.exOut
  exUnit.io.memIn := idwbUnit.io.memOut
  exUnit.io.wbIn := idwbUnit.io.wbOut
  exUnit.io.flush := exUnit.io.out.jump
  exUnit.io.enable := true.B&&(!io.load)

  memUnit.io.addr := exUnit.io.out
  memUnit.io.in := exUnit.io.memOut
  memUnit.io.wbIn := exUnit.io.wbOut
  memUnit.io.enable := true.B&&(!io.load)
  memUnit.io.ramPort.readData := io.ramPort.readData
}