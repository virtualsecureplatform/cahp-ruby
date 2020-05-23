import chisel3._

class VSPCore(implicit conf:CAHPConfig) extends Module {
  val io = IO(new Bundle{
    val finishFlag = Output(Bool())
    val mainRegOut = Output(new MainRegisterOutPort)
    val rom = new RomPort
    val ram = new RamPort
  })

  val core = Module(new CoreUnit)

  core.io.romPort.data := io.rom.data
  io.rom.addr := core.io.romPort.addr

  io.ram.writeEnable := core.io.ramPort.writeEnable
  io.ram.writeData := core.io.ramPort.writeData
  io.ram.addr := core.io.ramPort.addr

  core.io.ramPort.readData := io.ram.readData
  core.io.load := false.B

  io.finishFlag := core.io.finishFlag
  io.mainRegOut := core.io.mainRegOut
}
