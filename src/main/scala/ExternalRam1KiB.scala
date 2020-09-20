import chisel3._

class ExternalRam1KiB(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new Bundle {
    val port = Flipped(new RamPort)
    val debug = Output(Vec(512,UInt(16.W)))
  })

  val mem = Mem(512, UInt(16.W))

  when(io.port.writeEnable) {
    mem(io.port.addr) := io.port.writeData
  }

  io.port.readData := mem(io.port.addr)

  for(i <- 0 to 511){
    io.debug(i) := mem(i)
  }
}
