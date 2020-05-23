import chisel3._

class ForwardController(implicit val conf:CAHPConfig) extends Module{
  val io = IO(new Bundle{
    val rs = Input(UInt(4.W))
    val data = Input(UInt(16.W))
    val exWrite = Input(new MainRegInWrite())
    val memWrite = Input(new MainRegInWrite())
    val out = Output(UInt(16.W))
  })
  when(io.rs === io.exWrite.rd && io.exWrite.writeEnable) {
    //Forward from EX
    io.out := io.exWrite.writeData
  }.elsewhen(io.rs === io.memWrite.rd && io.memWrite.writeEnable) {
    //Forward from MEM
    io.out := io.memWrite.writeData
  }.otherwise{
    io.out := io.data
  }
}