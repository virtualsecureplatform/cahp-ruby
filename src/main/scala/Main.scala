object Main extends App{
  implicit val conf = CAHPConfig()
  conf.test = false
  chisel3.Driver.execute(args, () => new VSPCore())
}
