package top.srsea.peinture.draw

import android.graphics.Bitmap
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

val vl = """
/* use keyword let to declare a variable */
let Custom = Composite {
    Text('Hello') {
        width = 'wrap'
        height = 'wrap'
        id = 1
        textSize = '12dp'
        textColor = '#333333'
        Margin {
            top = '10dp'
        }
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tt = 'parent'
        }
    }
    Image {
        width = 'match'
        height = 'wrap'
        src = 'https://w.wallhaven.cc/full/6k/wallhaven-6k3oox.jpg'
        Margin {
            top = '10dp'
        }
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tb = 1
        }
    }
}

/*
 * There can only be one top-level declaration 
 */
Composite {
    // size = '300dp'  // width = height = '300dp'
    width = '300dp'
    height = 'wrap'
    Custom {
        color = '#F6F6F6'
        width = 'match'
        height = 'wrap'
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tt = 'parent'
            bb = 'parent'
        }
    }
}
""".trimIndent()

@RunWith(AndroidJUnit4::class)
class DrawTest {
    @Test
    fun testDraw() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val drawer = Drawer(appContext)
        val bitmap = drawer.drawBitmap(vl, Bitmap.Config.RGB_565)
        println(bitmap)
    }
}
