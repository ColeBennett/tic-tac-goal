package tictacgoal.client;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import tictacgoal.client.View.ViewType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootStack extends StackPane {

    private final Map<ViewType, Pane> panes = new HashMap<>();

    private ViewType current;

    public ViewType getCurrentView() {
        return current;
    }

    public boolean add(ViewType view, Pane pane) {
        Pane prev = panes.put(view, pane);
        if (current == null) {
            show(view);
        }
        return prev != null;
    }

    public Pane remove(ViewType view) {
        return panes.remove(view);
    }

    public void show(ViewType view) {
        Pane pane = panes.get(view);
        if (pane != null) {
            List<Node> children = getChildren();
            if (children.isEmpty()) {
                children.add(pane);
            } else {
                children.set(0, pane);
            }
            pane.prefWidthProperty().bind(widthProperty());
            pane.prefHeightProperty().bind(heightProperty());
            current = view;
        }
    }
}
