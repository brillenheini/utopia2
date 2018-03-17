package print

const val INIT = "\u001B\u0040"
const val BOLD_START = "\u001B\u0045\u0001"
const val ASTERISKS = "**"

/**
 * Highlight [terms] in this String by adding double asterisks.
 */
fun String.highlight(terms: List<String>): String {
    var x = this
    for (term in terms) {
        // $0 is the whole matched term
        x = x.replace(term.toRegex(RegexOption.IGNORE_CASE), "$ASTERISKS$0$ASTERISKS")
    }
    return x
}
