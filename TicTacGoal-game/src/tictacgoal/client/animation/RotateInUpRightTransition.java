package tictacgoal.client.animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Animate a rotate in up right effect on a node
 * 
 * Port of RotateInUpRight from Animate.css http://daneden.me/animate by Dan Eden
 * 
 * {@literal @}keyframes rotateInUpRight {
 * 	0% {
 * 		transform-origin: right bottom;
 * 		transform: rotate(-90deg);
 * 		opacity: 0;
 * 	}
 * 	100% {
 * 		transform-origin: right bottom;
 * 		transform: rotate(0);
 * 		opacity: 1;
 * 	}
 * }
 * 
 * @author Jasper Potts
 */
public class RotateInUpRightTransition extends CachedTimelineTransition {
    private Rotate rotate;
    /**
     * Create new RotateInUpRightTransition
     * 
     * @param node The node to affect
     */
    public RotateInUpRightTransition(final Node node) {
        super(node, null);
        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0.2));
    }

    @Override protected void starting() {
        super.starting();
        rotate = new Rotate(0,
                node.getBoundsInLocal().getWidth(),
                node.getBoundsInLocal().getHeight());
        timeline = TimelineBuilder.create()
            .keyFrames(
                new KeyFrame(Duration.millis(0),    
                    new KeyValue(node.opacityProperty(), 0, WEB_EASE),
                    new KeyValue(rotate.angleProperty(), -90, WEB_EASE)
                ),
                new KeyFrame(Duration.millis(1000),    
                    new KeyValue(node.opacityProperty(), 1, WEB_EASE),
                    new KeyValue(rotate.angleProperty(), 0, WEB_EASE)
                )
            )
            .build();
        node.getTransforms().add(rotate);
    }

    @Override protected void stopping() {
        super.stopping();
        node.getTransforms().remove(rotate);
    }
}
