import org.junit.Test
import top.srsea.peinture.vlparser.analysis.Analyzer
import top.srsea.peinture.vlparser.lex.Lexer
import top.srsea.peinture.vlparser.parse.Parser
import top.srsea.peinture.vlparser.token.End
import top.srsea.peinture.vlparser.token.Illegal
import top.srsea.peinture.vlparser.token.Token

class VLParserTest {
    private val vl = javaClass.getResource("test.vl").readText()

    @Test
    fun testLex() {
        val lexer = Lexer(vl)
        val tokens = mutableListOf<Token>()
        do {
            val token = lexer.lex()
            tokens.add(token)
        } while (
            token != Illegal && token != End
        )
        tokens.map { it::class.simpleName to it.literals }.forEach { println(it) }
    }

    @Test
    fun testParse() {
        val parser = Parser(vl)
        val root = parser.parse()
        println(root)
    }

    @Test
    fun testAnalyze() {
        val analyzer = Analyzer(vl)
        val root = analyzer.analyze()
        println(root)
    }
}
