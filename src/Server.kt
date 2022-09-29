import java.io.*
import java.net.ServerSocket
import java.net.Socket

fun main() {
    val server = Server(4435)
    server.startServer()

}
class Server {
    private var serverSocket: ServerSocket?
    private lateinit var lock: Any

    constructor(port: Int) {
        try {
            serverSocket = ServerSocket(port)
            lock = Object()
        } catch (ioe: IOException) {
            println(ioe)
            serverSocket = null
        }
    }

    fun startServer() {
        println("The server has started ")
        println("Listening on port 5543" )
        try {
            while (true) {
                val socket = serverSocket!!.accept()
                synchronized(lock) {
                    val client = SokobanClient(socket)
                    client.go()
                }
            }
        } catch (ioe: IOException) {
            println(ioe)
        }

    }
}

class SokobanClient : Runnable {
    private val file: FileResource
    private val socket: Socket
    private val thread: Thread

    constructor(socket: Socket) {
        this.socket = socket
        thread = Thread(this)
        file = FileResource()
    }

    fun go() {
        thread.start()
    }
    // "/home/developer/Umut_Arpidinov/Sokoban_Server/src/main/kotlin/levels/level"+level+".sok"
    override fun run() {
        try {

            val inputStream = socket.getInputStream()
            val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
            val input = BufferedReader(inputStreamReader)
            var level = input.readLine()

            val outputStream: OutputStream = socket.getOutputStream()
            val out = PrintWriter(outputStream)
            val answer =
                file.loadLevelFromFile("C:\\Users\\User\\IdeaProjects\\Server\\src\\Server\\levels\\level"+level+".sok")
            if (answer != null){
                out.println(answer)
                println("level"+level+ ".sok")
                println("I sent a message to client")
            }else{
                println("Android_iOS")
            }
            out.flush()
            inputStreamReader.close()
            input.close()
            out.close()
            outputStream.close()
            socket.close()

        } catch (e: IOException) {
            println(e)
        }

    }


}

class FileResource {
    fun loadLevelFromFile(filename: String): String? {
        var text = ""
        var file = File(filename)
        var size = file.length().toInt()
        var array: CharArray? = CharArray(size)
        val fileIn = FileInputStream(filename)
        try {
            var unicode: Int
            var index = 0
            while (fileIn.read().also { unicode = it } != -1) {
                val symbol = unicode.toChar()
                if ('0' <= symbol && symbol <= '4') {
                    array!![index] = symbol
                    index = index + 1
                } else if (symbol == '\n') {
                    array!![index] = 'A'
                    index += 1
                }
            }
            if (array!![index] != '\n') {
                array!![index] = 'A'
            }
            text = String(array, 0, index)
            array = null
            fileIn.close()
            return text

        } catch (e: FileNotFoundException) {
            println(e)
        }

        return text
    }
}