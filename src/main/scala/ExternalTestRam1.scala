import chisel3._

class ExternalTestRam1(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new Bundle {
    val port = Flipped(new RamPort)
    val load = Input(Bool())
  })

  val mem = Mem(256, UInt(16.W))

  val loadAddr = RegInit(0.U(8.W))
  val ram = VecInit(conf.testRam map (x=> x.U(conf.ramDataWidth.W)))

  val regWriteEnable = RegInit(false.B)
  val regWriteAddr = RegInit(0.U(conf.ramAddrWidth.W))
  val regWriteData = RegInit(0.U(conf.ramDataWidth.W))

  regWriteEnable := io.port.writeEnable
  regWriteAddr := io.port.addr
  regWriteData := io.port.writeData

  when(io.load){
    mem(loadAddr) := ram(loadAddr)
    loadAddr := loadAddr + 1.U
  }.otherwise {
    when(regWriteEnable) {
      mem(regWriteAddr) := regWriteData
      when(conf.debugMem.B) {
        printf("[MEM] MemWrite Mem[0x%x] <= Data:0x%x\n", regWriteAddr, regWriteData)
      }
    }
  }

  io.port.readData := mem(io.port.addr)
}
