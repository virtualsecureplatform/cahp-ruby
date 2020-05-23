/*
Copyright 2019 Naoki Matsumoto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import chisel3._

class MainRegInRead(implicit val conf:CAHPConfig) extends Bundle {
  val rs1 = UInt(4.W)
  val rs2 = UInt(4.W)
}

class MainRegInWrite(implicit val conf:CAHPConfig) extends Bundle {
  val rd = UInt(4.W)
  val writeEnable = Bool()
  val writeData = UInt(16.W)
}

class MainRegOut(implicit val conf:CAHPConfig) extends Bundle {
  val rs1Data = UInt(16.W)
  val rs2Data = UInt(16.W)
}

class MainRegPort(implicit val conf:CAHPConfig) extends Bundle {
  val inRead = Input(new MainRegInRead)
  val inWrite = Input(new MainRegInWrite)

  val out = Output(new MainRegOut)
}

class MainRegisterPort(implicit val conf:CAHPConfig) extends Bundle {
  val port = new MainRegPort
  val regOut = new MainRegisterOutPort
}

class MainRegister(implicit val conf:CAHPConfig) extends Module{
  val io = IO(new MainRegisterPort)

  val MainReg = Mem(16, UInt(16.W))

  io.port.out.rs1Data := MainReg(io.port.inRead.rs1)
  io.port.out.rs2Data := MainReg(io.port.inRead.rs2)
  when(io.port.inWrite.writeEnable){
    MainReg(io.port.inWrite.rd) := io.port.inWrite.writeData
    when(conf.debugWb.B){
      printf("portA Reg x%d <= 0x%x\n", io.port.inWrite.rd, io.port.inWrite.writeData)
    }
  }

  io.regOut.x0 := MainReg(0)
  io.regOut.x1 := MainReg(1)
  io.regOut.x2 := MainReg(2)
  io.regOut.x3 := MainReg(3)
  io.regOut.x4 := MainReg(4)
  io.regOut.x5 := MainReg(5)
  io.regOut.x6 := MainReg(6)
  io.regOut.x7 := MainReg(7)
  io.regOut.x8 := MainReg(8)
  io.regOut.x9 := MainReg(9)
  io.regOut.x10 := MainReg(10)
  io.regOut.x11 := MainReg(11)
  io.regOut.x12 := MainReg(12)
  io.regOut.x13 := MainReg(13)
  io.regOut.x14 := MainReg(14)
  io.regOut.x15 := MainReg(15)
}
