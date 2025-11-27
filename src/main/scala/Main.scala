object Main extends App{
  implicit val conf = CAHPConfig()
  conf.test = false
  (new chisel3.stage.ChiselStage).emitVerilog(new VSPCore(), args)
  (new chisel3.stage.ChiselStage).emitVerilog(new ExternalRam(), args)
  (new chisel3.stage.ChiselStage).emitVerilog(new ExternalRam1KiB(), args)
}
