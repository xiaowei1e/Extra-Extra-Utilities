package eeu.util;

import arc.math.Mathf;
import arc.math.geom.Vec2;

//copied from fengkeyleaf
//:(
public class Mathe {
    private static boolean isOverlapButHavingCommonEndPoint(Line line1, Line line2) {
        return sortByX(line2.startPoint, line2.endPoint) &&
                line1.startPoint.equals(line2.endPoint) ||
                line1.endPoint.equals(line2.startPoint);
    }

    private static boolean ifLinesIntersect(Line line1, Line line2, double res1, double res2) {
        // parallel cases:
        // case 1: overlap or on the same line.
        if (Mathf.zero(res1) &&
                Mathf.zero(res2))
            return isOverlapButHavingCommonEndPoint(line1, line2);
        // case 2: parallel on the right side.
        if (res1 < 0 && res2 < 0)
            return false;
        // case 3: parallel on the left side.
        return !(res1 > 0) || !(res2 > 0);

        // intersecting cases: either intersect at
        // a common point other than endpoints,
        // or at one of the endpoints.
    }

    /**
     * line1 and line2 intersects?
     */

    public static boolean ifLinesIntersect(Line line1, Line line2) {
        if (line1 == null || line2 == null) return false;

        // to left test based on line1.
        double res1 = triangleArea(line1.endPoint, line1.startPoint, line2.endPoint);
        double res2 = triangleArea(line1.endPoint, line1.startPoint, line2.startPoint);
        // to left test based on line2.
        double res3 = triangleArea(line2.endPoint, line2.startPoint, line1.endPoint);
        double res4 = triangleArea(line2.endPoint, line2.startPoint, line1.startPoint);

        // have intersection if and only if
        // two endpoints of one line are
        // at the opposite side of the other line.
        boolean finalRes1 = ifLinesIntersect(line1, line2, res1, res2);
        boolean finalRes2 = ifLinesIntersect(line1, line2, res3, res4);
        return finalRes1 && finalRes2;
    }

    public static float triangleArea(Vec2 point1, Vec2 point2, Vec2 point3) {
        return point1.x * point2.y - point1.y * point2.x +
                point2.x * point3.y - point2.y * point3.x +
                point3.x * point1.y - point3.y * point1.x;
    }

    public static boolean sortByX(Vec2 p1, Vec2 p2) {
        if (Mathf.equal(p1.x, p2.x))
            return p1.y > p2.y;
        return p1.x > p2.x;
    }

    public static class Line {
        public Vec2 startPoint;
        public Vec2 endPoint;

        public Line() {
            this.startPoint = new Vec2();
            this.endPoint = new Vec2();
        }

        public Line(Vec2 startPoint, Vec2 endPoint) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
        }

        public void set(float x1, float y1, float x2, float y2) {
            startPoint.set(x1, y1);
            endPoint.set(x2, y2);
        }
    }
}
