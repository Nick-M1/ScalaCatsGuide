package Starter.whyMonads

object demo1 {

  // Class which prevents multi-threaded access to a value:
  case class SafeWrapper[+T](private val internalValue: T) {
    def transform[S](transformer: T => SafeWrapper[S]): SafeWrapper[S] = synchronized {
      transformer(internalValue)
    }
  }

  // Using SafeWrapper to set, transform & get the value inside
  val safeString2: SafeWrapper[String] = SafeWrapper("Scala is awesome")
  val usingSafeString2: SafeWrapper[String] = safeString2.transform(string => SafeWrapper(string.toUpperCase()))


}
