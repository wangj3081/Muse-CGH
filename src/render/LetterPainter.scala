package render

import java.awt.{Color, Graphics2D}

import main.CurveDrawer
import utilities.Vec2

/**
  * Created by weijiayi on 3/4/16.
  */
class LetterPainter(g2d: Graphics2D, pixelPerUnit: Double, displayPixelScale: Double, imageOffset: Vec2,
                    dotsPerUnit:Double, thicknessScale: Double) {

  def draw(segs: IndexedSeq[RenderingSeg], offset: Vec2, color: Color): Unit = {
    def pointTrans(p: Vec2): Vec2 = {
      val s = pixelPerUnit*displayPixelScale
      (p+offset)*s + imageOffset
    }

    val drawer = new CurveDrawer(g2d, pointTrans, pixelPerUnit*displayPixelScale, dotsPerUnit, thicknessScale)

    drawer.setColor(color)
    segs.foreach{ case RenderingSeg(curve, wf) =>
      drawer.drawCurveWithWF(curve, wf)
    }
  }

  def drawAndBuffer(bufferScaleFactor: Double, bufferG: Graphics2D, timeUsed: Double => Unit = (_) => Unit)(
    segs: IndexedSeq[RenderingSeg], offset: Vec2, color: Color): Unit = {
    val s = pixelPerUnit*displayPixelScale
    def pointTrans(p: Vec2): Vec2 = {
      (p+offset)*s + imageOffset
    }

    def bufferPointTrans(p: Vec2): Vec2 = pointTrans(p) * bufferScaleFactor

    val drawer = new CurveDrawer(g2d, pointTrans, s, dotsPerUnit, thicknessScale)
    val bufferDrawer = new CurveDrawer(bufferG, bufferPointTrans, s * bufferScaleFactor, dotsPerUnit, thicknessScale)

    drawer.setColor(color)
    bufferDrawer.setColor(color)

    var lastPos = Vec2.zero
    segs.foreach{case RenderingSeg(curve, wf) =>
      val dis = (curve.p0 - lastPos).length
      lastPos = curve.p0

      timeUsed(dis)
      drawer.drawCurveWithWF(curve, wf, timeUsed)
      bufferDrawer.drawCurveWithWF(curve, wf)
    }
  }
}
