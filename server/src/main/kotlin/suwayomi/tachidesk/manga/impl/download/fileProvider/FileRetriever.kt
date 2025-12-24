package suwayomi.tachidesk.manga.impl.download.fileProvider

import java.io.InputStream

fun interface RetrieveFile {
    fun executeGetImage(vararg args: Any): Pair<InputStream, String>
}

fun interface RetrieveFile0Args : RetrieveFile {
    fun execute(): Pair<InputStream, String>

    override fun executeGetImage(vararg args: Any): Pair<InputStream, String> = execute()
}

@Suppress("UNCHECKED_CAST")
fun interface RetrieveFile1Args<A> : RetrieveFile {
    fun execute(a: A): Pair<InputStream, String>

    override fun executeGetImage(vararg args: Any): Pair<InputStream, String> = execute(args[0] as A)
}

fun interface RetrieveFileFile {
    fun executeGetImage(vararg args: Any): Pair<Any, String>
}

fun interface RetrieveFileFile0Args : RetrieveFileFile {
    fun execute(): Pair<Any, String>

    override fun executeGetImage(vararg args: Any): Pair<Any, String> = execute()
}


@Suppress("UNCHECKED_CAST")
fun interface RetrieveFileFile1Args<A> : RetrieveFileFile {
    fun execute(a: A): Pair<Any, String>

    override fun executeGetImage(vararg args: Any): Pair<Any, String> = execute(args[0] as A)
}

@Suppress("UNCHECKED_CAST")
fun interface RetrieveFile2Args<A, B> : RetrieveFile {
    fun execute(
        a: A,
        b: B,
    ): Pair<InputStream, String>

    override fun executeGetImage(vararg args: Any): Pair<InputStream, String> = execute(args[0] as A, args[1] as B)
}

fun interface FileRetriever {
    fun getImage(): RetrieveFile
    fun getImageFile(): RetrieveFileFile
}
