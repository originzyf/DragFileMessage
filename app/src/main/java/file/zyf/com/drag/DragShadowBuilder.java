package file.zyf.com.drag;

import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.View;

public class DragShadowBuilder extends View.DragShadowBuilder {

    final Point touchPoint = new Point();

    public DragShadowBuilder(View view, Point touchPoint) {
        super(view);
        this.touchPoint.set(touchPoint.x, touchPoint.y);
    }

    @Override
    public void onProvideShadowMetrics(@NonNull Point shadowSize, @NonNull Point shadowTouchPoint) {
        final View view = getView();
        if (view != null) {
            shadowSize.set(view.getWidth(), view.getHeight());
            shadowTouchPoint.set(touchPoint.x, touchPoint.y);
        }
    }

    @Override
    public void onDrawShadow(@NonNull Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
