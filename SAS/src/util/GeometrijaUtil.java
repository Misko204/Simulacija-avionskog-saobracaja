package util;

import java.awt.Point;

public class GeometrijaUtil {
	// Koordinate sa mape se prevode u koordinate sa ekrana
	public static Point modelToScreen(double x, double y, int width, int height) {
		int margin = 40;
		
		int screenWidth = width - 2 * margin;
		int screenHeight = height - 2 * margin;
		
		int screenX = (int) (margin + ((x + 90) / 180) * screenWidth);
		int screenY = (int) (margin + ((90 - y) / 180) * screenHeight);
		
		return new Point(screenX, screenY);
	}
	
	// Proverava se da li su koordinate unutar kvadrata
	public static boolean tackaUKvadratu(int px, int py, int cx, int cy, int size) {
		int halfSize = size / 2;
		return px >= cx - halfSize && px <= cx + halfSize &&
				py >= cy - halfSize && py <= cy + halfSize;
	}
}
