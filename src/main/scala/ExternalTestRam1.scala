import chisel3._

class ExternalTestRam1(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new Bundle {
    val port = Flipped(new RamPort)
    val load = Input(Bool())
  })

  val mem = Mem(256, UInt(16.W))

  val loadAddr = RegInit(0.U(8.W))
  val ram = VecInit(conf.testRam map (x=> x.U(conf.ramDataWidth.W)))
  when(io.load){
    mem(loadAddr) := ram(loadAddr)
    loadAddr := loadAddr + 1.U
  }.otherwise {
    when(io.port.writeEnable) {
      mem(io.port.addr) := io.port.writeData
      when(conf.debugMem.B) {
        printf("[MEM] MemWrite Mem[0x%x] <= Data:0x%x\n", io.port.addr, io.port.writeData)
      }
    }
  }

  io.port.readData := mem(io.port.addr)
}
