package org.sadtech.vkbot.core.distribution;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.objects.board.TopicComment;
import org.sadtech.bot.core.domain.content.BoardComment;
import org.sadtech.bot.core.service.BoardCommentService;
import org.sadtech.vkbot.core.convert.Convert;
import org.sadtech.vkbot.core.convert.TopicCommentToBoardComment;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardCommentSubscribe extends AbstractBasketSubscribe<JsonObject, TopicComment> {

    private final BoardCommentService boardCommentService;
    private final Convert<TopicComment, BoardComment> topicConvert = new TopicCommentToBoardComment();
    private Set<Integer> answerTopicsId;
    private Set<Integer> noAnswerPersonId;
    private Boolean respondAppeal = true;

    public BoardCommentSubscribe(BoardCommentService boardCommentService) {
        this.boardCommentService = boardCommentService;
        this.convert = (object) -> {
            Gson gson = new Gson();
            return gson.fromJson(object.getAsJsonObject("object"), TopicComment.class);
        };
    }

    public Set<Integer> getAnswerTopicsId() {
        return answerTopicsId;
    }

    public void setAnswerTopicsId(Set<Integer> answerTopicsId) {
        this.answerTopicsId = answerTopicsId;
    }

    public Set<Integer> getNoAnswerPersonId() {
        return noAnswerPersonId;
    }

    public void setNoAnswerPersonId(Set<Integer> noAnswerPersonId) {
        this.noAnswerPersonId = noAnswerPersonId;
    }

    public Boolean getRespondAppeal() {
        return respondAppeal;
    }

    public void setRespondAppeal(Boolean respondAppeal) {
        this.respondAppeal = respondAppeal;
    }

    @Override
    protected boolean check(JsonObject object) {
        String type = object.get("type").getAsString();
        String message = object.getAsJsonObject("object").get("text").getAsString();
        Integer groupId = object.get("group_id").getAsInt();
        return "board_post_new".equals(type) && checkRespondAppeal(message, groupId);
    }

    private boolean checkRespondAppeal(String message, Integer groupId) {
        if (respondAppeal) {
            Pattern pattern = Pattern.compile("\\[club" + groupId);
            Matcher m = pattern.matcher(message);
            return m.find();
        } else {
            return true;
        }
    }

    private boolean checkTopic(Integer topicId) {
        if (answerTopicsId == null) {
            return true;
        } else {
            return answerTopicsId.contains(topicId);
        }
    }

    private boolean checkPerson(Integer personId) {
        if (noAnswerPersonId == null) {
            return true;
        } else {
            return !noAnswerPersonId.contains(personId);
        }
    }


    @Override
    public void processing(TopicComment object) {
        if (checkPerson(object.getFromId()) && checkTopic(object.getTopicId())) {
            boardCommentService.add(topicConvert.converting(object));
        }
    }
}
