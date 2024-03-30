package de.royzer.fabrichg.proxy

import com.mojang.logging.LogUtils
import de.royzer.fabrichg.settings.ConfigManager
import net.silkmc.silk.core.logging.logger
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

class ProxyManager(val host: String?, val port: Int) {
    fun sendStatus(status: Int){
        Thread{
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(host,port), 10000)
                val output = PrintWriter(socket.getOutputStream())
                output.println("STATUS")
                output.println(ConfigManager.serverInfoData.serverName)
                output.println(status.toString())
                output.flush()
                socket.close()
                println("Send status $status to proxy")
            }catch (ex :Exception){
                println(ex.message)
            }
        }.start()
    }


    object ServerStatus{
        const val REACHABLE = 1
        const val UNREACHABLE = 0
    }
}