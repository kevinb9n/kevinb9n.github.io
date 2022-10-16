package site.kevinb9n.plane

import com.google.common.collect.Collections2
import com.google.common.collect.HashMultiset
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.jupiter.api.Test
import site.kevinb9n.plane.Vector2.Companion.vector

class EnclosingCircleTest {
  @Test
  fun test() {
    assertThat(enclosingCircle(Point2.ORIGIN)).isEqualTo(Circle(Point2.ORIGIN, 0.0))
    assertThat(enclosingCircle(Point2(2, 3), Point2(10, 9))).isEqualTo(Circle(Point2(6, 6), 5.0))
    assertThat(enclosingCircle(listOf(
      Point2(5, 0), Point2(3, 4), Point2(4, 3), Point2(0, 5), Point2(-3, 4), Point2(-4, 3), Point2(-5, 0))))
      .isEqualTo(Circle(Point2.ORIGIN, 5.0))
  }

  @Test
  fun dafuq() {
    val pts = listOf(
      Point2(554.1518237937553, 584.9494469567428),
      Point2(204.94628334631685, 286.8169294888452),
      Point2(307.7983925624817, 566.3972041378483),
      Point2(436.0277954210054, 222.59276893818472))

    Collections2.permutations(pts).forEach {
      val circ = enclosingCircle(it)
      assertThat(circ.center.x).isWithin(1e-12).of(379.5490535700361)
      assertThat(circ.center.y).isWithin(1e-12).of(435.883188222794)
      assertThat(circ.radius).isWithin(1e-12).of(229.57978321867427)
    }
  }

  @Test
  fun twoBug() {
    val pt1 = Point2(567.9002227167875, 420.17797057277977)
    val pt2 = Point2(484.64316482608444, 459.19045934543453)
    val c = Point2(526.271693771436, 439.6842149591072)
    assertThat(enclosingCircle(pt1, pt2)).isEqualTo(Circle(c, 45.97203489310761))
  }

  @Test
  fun phuctup() {
    val pts = listOf(
      Point2(207.1170409579347, 754.8549269718503),
      Point2(239.13423064571052, 779.5550175241485),
      Point2(313.82006004850894, 319.14001361931685),
      Point2(464.1112454134134, 793.6092370600056),
      Point2(754.2597253338755, 392.4303535825119),
      Point2(754.4159916756881, 755.3336738384503),
    )

    val multiset = HashMultiset.create<Double>()
    multiset += Collections2.permutations(pts).map {
      enclosingCircle(it).radius
      //== Circle(Point2(480.9249278223155, 573.9997454481017), 328.1453269408627)
    }
    assertWithMessage(multiset.toString()).that(multiset.elementSet().size).isEqualTo(1) // lol
  }

  @Test
  fun phuctup2() {
    var pts = listOf(
      Point2(207.1170409579347, 754.8549269718503),
      Point2(239.13423064571052, 779.5550175241485),
      Point2(313.82006004850894, 319.14001361931685),
      Point2(464.1112454134134, 793.6092370600056),
      Point2(754.2597253338755, 392.4303535825119),
      Point2(754.4159916756881, 755.3336738384503),
    )
    pts = pts + (pts[2] + vector(30, 30))
    pts = pts + (pts[2] + vector(60, 60))
    pts = pts + (pts[2] + vector(90, 90))

    val multiset = HashMultiset.create<Double>()
    multiset += Collections2.permutations(pts).map {
      enclosingCircle(it).radius
      //== Circle(Point2(480.9249278223155, 573.9997454481017), 328.1453269408627)
    }
    assertWithMessage(multiset.toString()).that(multiset.elementSet().size).isEqualTo(1) // lol
  }

  @Test
  fun isOneOutside() {
    val pts = listOf(
      Point2(x = 633.4392337033598, y = 241.57556384887064),
      Point2(x = 645.8726698471279, y = 598.3696531290592),
      Point2(x = 317.5138710890178, y = 721.9860391418614),
      Point2(x = 294.32518185785733, y = 215.06746181460855),
      Point2(x = 536.9761558130833, y = 494.92015881297584),
      Point2(x = 741.3190328790125, y = 373.4926128147167),
      Point2(x = 335.13728053192153, y = 511.5231731330061),
      Point2(x = 691.3412414543294, y = 710.205148665164),
      Point2(x = 581.4477839128549, y = 466.08587074534825),
      Point2(x = 503.9315825345246, y = 465.21164993878125),
      Point2(x = 358.89717545427607, y = 728.2136452347783),
      Point2(x = 236.89139661345288, y = 745.9377693379422),
      Point2(x = 263.329477589005, y = 331.56363498530067),
      Point2(x = 443.0924563396935, y = 535.5220407868871),
      Point2(x = 518.8926537707633, y = 358.86526338316474),
      Point2(x = 262.89658321891636, y = 503.40820513908534),
      Point2(x = 419.1125818200137, y = 716.6509230604088),
      Point2(x = 493.84473804372755, y = 439.29929235744993),
      Point2(x = 227.1061151266274, y = 315.5841478368034),
      Point2(x = 377.67301836381057, y = 563.372726486895),
      Point2(x = 711.2211069370037, y = 463.48361523855476))
    val circle = enclosingCircle(pts)
//    assertThat(circle).isEqualTo(
//      Circle(center=Point2(x=447.6552538530455, y=503.57670885835364), radius=321.1857517937279))
    val filter = pts.filter {
      val result = it !in circle
      if (result) {
        println("${it.distance(circle.center)} > ${circle.radius}")
      }
      result
    }
    assertThat(filter).isEmpty()
  }
  @Test
  fun sus() {
    val pts = listOf(
      Point2(x=786.7949221448969, y=321.2513553241638),
      Point2(x=269.72385312172753, y=399.7408590414084),
      Point2(x=587.5121689484854, y=745.8680580670527),
      Point2(x=786.2456768224041, y=525.2220779897896),
      Point2(x=771.334136220995, y=743.6789214429316),
      Point2(x=201.66423241583456, y=543.069990164538),
      Point2(x=504.6941195885293, y=260.46194777264463),
      Point2(x=272.9946983107953, y=774.9622752316835),
      Point2(x=734.9210023269793, y=348.85989939703563),
      Point2(x=709.6942777500349, y=473.26942740007865),
      Point2(x=475.41954590979884, y=226.15776632875315),
      Point2(x=664.5001905327106, y=314.31695874764387),
      Point2(x=263.4737812229181, y=240.9834695456211),
      Point2(x=251.7336014822376, y=532.4558656205759),
      Point2(x=790.4291597702031, y=694.0366352168785),
      Point2(x=456.2678817652389, y=717.032330412291),
      Point2(x=252.94051413370843, y=247.98776021199023),
      Point2(x=642.2588532517916, y=306.5070529543023),
      Point2(x=292.96726444098516, y=769.6645243005804),
      Point2(x=782.531702863248, y=421.46221698330174),
      Point2(x=241.36395750063735, y=650.52911510219),
      Point2(x=720.6918673234095, y=537.3342272995118),
      Point2(x=711.7717542212192, y=759.4038020967014),
      Point2(x=357.77781782630996, y=269.17376657922694),
      Point2(x=214.10994549707215, y=381.75365142033513),
      Point2(x=459.19932300581235, y=782.9056757654737),
      Point2(x=277.0417911184867, y=618.0192770610579),
      Point2(x=757.5766290866719, y=696.8807106054351),
      Point2(x=257.6585127521507, y=356.3524156588464),
      Point2(x=789.3850782168646, y=409.5549143291435),
      Point2(x=242.78229734848338, y=382.3787441557256),
      Point2(x=786.2434904366631, y=450.50966914748324),
      Point2(x=671.9906634974141, y=780.3802313193945),
      Point2(x=636.1540376686694, y=205.61947100846066),
      Point2(x=750.9534611632434, y=771.1780489952985),
      Point2(x=283.7230728436293, y=535.9385344299747),
      Point2(x=201.88984352027828, y=235.04628737604827),
    )
    for (i in 1..10000) {
      val shuff = pts.shuffled()
      val enclose = enclosingCircle(shuff)
      val mesg = shuff.joinToString("") { "$it,\n" }
      assertWithMessage(mesg).that(enclose.center.x).isWithin(1e-12).of(476.4216523417608)
      assertWithMessage(mesg).that(enclose.center.y).isWithin(1e-12).of(503.1121681856734)
      assertWithMessage(mesg).that(enclose.radius).isWithin(1e-12).of(383.70174681519916)
    }
  }
}
