import java.io.PrintWriter
import java.util.UUID

import scala.io.Source


class TestBinParser(filePath: String) {
  val source = Source.fromFile(filePath)
  val memAhex = "/tmp/"+UUID.randomUUID().toString+".hex.txt"
  val memBhex = "/tmp/"+UUID.randomUUID().toString+".hex.txt"
  val lines = source.getLines

  val memSize = 256

  var memAData = Map[BigInt, BigInt]()
  var memBData = Map[BigInt, BigInt]()
  var memData = Map[BigInt, BigInt]()
  var romData = Map[BigInt, BigInt]()
  var res = 0
  var cycle = 0

  var romSeq:Seq[BigInt] = Seq()
  var ramSeq:Seq[BigInt] = Seq()
  var memASeq:Seq[BigInt] = Seq()
  var memBSeq:Seq[BigInt] = Seq()
  lines.foreach(s => parseLine(s))
  for(i <- 0 to romData.size-1){
    romSeq = romSeq:+romData(i)
  }

  for(i <- 0 to memSize-1) {
    if (i == 0x1FE/2) {
      ramSeq = ramSeq :+ BigInt(0x1E8)
    } else if (i == 0x1E8/2){
      ramSeq = ramSeq :+ BigInt(0x1)
    } else if (memData.contains(i)) {
      ramSeq = ramSeq :+ memData(i)
    } else {
      ramSeq = ramSeq :+ BigInt(0)
    }
  }

  for(i <- 0 to memSize-1){
    if(memAData.contains(i)){
      memASeq = memASeq:+memAData(i)
    }else{
      memASeq = memASeq:+BigInt(0)
    }

    if(memBData.contains(i)){
      memBSeq = memBSeq:+memBData(i)
    }else{
      memBSeq = memBSeq:+BigInt(0)
    }
  }

  val memApw = new PrintWriter(memAhex)
  val memBpw = new PrintWriter(memBhex)
  for(i <- 0 to memSize-1){
    memApw.println(memASeq(i).toString(16))
    memBpw.println(memBSeq(i).toString(16))
  }
  memApw.close
  memBpw.close

  def asUnsigned(unsignedLong: Long) =
    (BigInt(unsignedLong >>> 1) << 1) + (unsignedLong & 1)

  def parseLine(line:String){
    val tokens = line.split(" ", 0)
    if(tokens.length == 3){
      val addr = BigInt(java.lang.Long.parseUnsignedLong(tokens(1), 16))
      val data = asUnsigned(java.lang.Long.parseUnsignedLong(tokens(2), 16))
      if(tokens(0).contains("ROM")){
        romData += (addr->data)
      }
      else if(tokens(0).contains("RAM")){
        memData += (addr->data)
      }
    }
    else if(tokens.length == 2){
      if(tokens(0).contains("FIN")){
        res = Integer.parseUnsignedInt(tokens(1), 16)
      }
      else if(tokens(0).contains("CYCLE")){
        cycle = Integer.parseUnsignedInt(tokens(1), 16)
      }
    }
  }
}
