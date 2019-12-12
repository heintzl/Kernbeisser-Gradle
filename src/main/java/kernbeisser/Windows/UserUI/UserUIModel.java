package kernbeisser.Windows.UserUI;

import kernbeisser.DBEntitys.User;
import kernbeisser.Enums.UserPersistFeedback;
import kernbeisser.Windows.Model;

import java.util.function.Consumer;

public class UserUIModel implements Model {
    private User loaded = new User();
    private Consumer<UserPersistFeedback> feedbackConsumer;

    UserUIModel(Consumer<UserPersistFeedback> feedbackConsumer){
        this.feedbackConsumer=feedbackConsumer;
    }


    public User getLoaded() {
        return loaded;
    }

    public void setLoaded(User loaded) {
        this.loaded = loaded;
    }

}
