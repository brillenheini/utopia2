package print

const val INIT = "\u001B\u0040"
const val BOLD_START = "\u001B\u0045\u0001"
const val BOLD_END = "\u001B\u0045\u0000"
const val UNDERLINE_START = "\u001B\u002D\u0001"
const val UNDERLINE_END = "\u001B\u002D\u0000"

/**
 * Highlight [terms] in this String by adding underline marks for the printer.
 */
fun String.highlight(terms: List<String>): String {
    var x = this
    for (term in terms) {
        // $0 is the whole matched term
        x = x.replace(term.toRegex(RegexOption.IGNORE_CASE), "$UNDERLINE_START$0$UNDERLINE_END")
    }
    return x
}
