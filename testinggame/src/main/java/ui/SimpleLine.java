package ui;

/**
 * Simple Line class to replace JavaFX Line for testing purposes
 * This class can be extended to work with JavaFX when needed
 */
public class SimpleLine {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private Object fxLine; // Reference to JavaFX Line if available
    
    public SimpleLine() {
        this.startX = 0;
        this.startY = 0;
        this.endX = 0;
        this.endY = 0;
    }
    
    public SimpleLine(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
    
    // Constructor that accepts JavaFX Line
    public SimpleLine(Object fxLine) {
        this.fxLine = fxLine;
        // Try to extract coordinates from JavaFX Line if possible
        try {
            if (fxLine != null) {
                // Use reflection to get coordinates from JavaFX Line
                java.lang.reflect.Method getStartX = fxLine.getClass().getMethod("getStartX");
                java.lang.reflect.Method getStartY = fxLine.getClass().getMethod("getStartY");
                java.lang.reflect.Method getEndX = fxLine.getClass().getMethod("getEndX");
                java.lang.reflect.Method getEndY = fxLine.getClass().getMethod("getEndY");
                
                this.startX = (Double) getStartX.invoke(fxLine);
                this.startY = (Double) getStartY.invoke(fxLine);
                this.endX = (Double) getEndX.invoke(fxLine);
                this.endY = (Double) getEndY.invoke(fxLine);
            }
        } catch (Exception e) {
            // Fallback to default values
            this.startX = 0;
            this.startY = 0;
            this.endX = 0;
            this.endY = 0;
        }
    }
    
    public double getStartX() { return startX; }
    public void setStartX(double startX) { 
        this.startX = startX; 
        // Update JavaFX Line if available
        updateFxLine();
    }
    
    public double getStartY() { return startY; }
    public void setStartY(double startY) { 
        this.startY = startY; 
        updateFxLine();
    }
    
    public double getEndX() { return endX; }
    public void setEndX(double endX) { 
        this.endX = endX; 
        updateFxLine();
    }
    
    public double getEndY() { return endY; }
    public void setEndY(double endY) { 
        this.endY = endY; 
        updateFxLine();
    }
    
    // Get the underlying JavaFX Line if available
    public Object getFxLine() { return fxLine; }
    public void setFxLine(Object fxLine) { this.fxLine = fxLine; }
    
    // Update JavaFX Line coordinates
    private void updateFxLine() {
        if (fxLine != null) {
            try {
                java.lang.reflect.Method setStartX = fxLine.getClass().getMethod("setStartX", double.class);
                java.lang.reflect.Method setStartY = fxLine.getClass().getMethod("setStartY", double.class);
                java.lang.reflect.Method setEndX = fxLine.getClass().getMethod("setEndX", double.class);
                java.lang.reflect.Method setEndY = fxLine.getClass().getMethod("setEndY", double.class);
                
                setStartX.invoke(fxLine, startX);
                setStartY.invoke(fxLine, startY);
                setEndX.invoke(fxLine, endX);
                setEndY.invoke(fxLine, endY);
            } catch (Exception e) {
                // Ignore if JavaFX Line methods are not available
            }
        }
    }
    
    // Static method to create SimpleLine from JavaFX Line
    public static SimpleLine fromFxLine(Object fxLine) {
        return new SimpleLine(fxLine);
    }
}
