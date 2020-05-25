import chisel3._

class ExternalRam(implicit val conf:CAHPConfig) extends Module {
  val io = IO(new Bundle {
    val port = Flipped(new RamPort)
    val debug = Output(Vec(256,UInt(16.W)))
  })

  val mem = Mem(256, UInt(16.W))

  when(io.port.writeEnable) {
    mem(io.port.addr) := io.port.writeData
  }

  io.port.readData := mem(io.port.addr)

  for(i <- 0 to 255){
    io.debug(i) := mem(i)
  }
}
