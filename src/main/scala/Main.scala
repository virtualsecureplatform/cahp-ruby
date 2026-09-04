object Main extends App{
  val defaults = CAHPConfig()
  implicit val conf: CAHPConfig = CAHPConfig(
    romAddrWidth = sys.props.get("cahp.romAddrWidth").map(_.toInt).getOrElse(defaults.romAddrWidth),
    ramAddrWidth = sys.props.get("cahp.ramAddrWidth").map(_.toInt).getOrElse(defaults.ramAddrWidth),
  )
  conf.test = false
  (new chisel3.stage.ChiselStage).emitVerilog(new VSPCore(), args)
  (new chisel3.stage.ChiselStage).emitVerilog(new ExternalRam(), args)
  (new chisel3.stage.ChiselStage).emitVerilog(new ExternalRam1KiB(), args)
}
