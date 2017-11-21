package tictacgoal.client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class View implements Initializable {

    public enum ViewType {
        MENU("menu"),
        MULTIPLAYER("multiplayer"),
        BOARD("board"),
        END("end");

        private final String fxml;

        ViewType(String fxml) {
            this.fxml = fxml + ".fxml";
        }

        public String getFxml() {
            return fxml;
        }
    }

    private boolean loaded;
    private Map<String, Object> namespace;
    private FXMLLoader loader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        namespace = loader.getNamespace();
        loader = null;
        initialize();
        loaded = true;
    }

    public boolean hasLoaded() {
        return loaded;
    }

    public Map<String, Object> getNamespace() {
        return namespace;
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T getNode(String fxid) {
        return (T) namespace.get(fxid);
    }

    void setLoader(FXMLLoader loader) {
        this.loader = loader;
    }

    protected abstract void initialize();
}
