import chisel3._

class TopUnit(implicit conf:CAHPConfig) extends Module {
  val io = IO(new Bundle{
    val testRegx8 = Output(UInt(16.W))
    val finishFlag = Output(Bool())
    val load = Input(Bool())
  })

  val core = Module(new CoreUnit)
  val rom = Module(new ExternalTestRom)
  val ram = Module(new ExternalTestRam1)

  core.io.romPort.data := rom.io.data
  rom.io.addr := core.io.romPort.addr

  ram.io.port.writeEnable := core.io.ramPort.writeEnable
  ram.io.port.writeData := core.io.ramPort.writeData
  ram.io.port.addr := core.io.ramPort.addr
  ram.io.load := io.load

  core.io.ramPort.readData := ram.io.port.readData
  core.io.load := io.load

  io.testRegx8 := core.io.testRegx8
  io.finishFlag := core.io.finishFlag
}
