package org.sadtech.vkbot.core.sender;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.sadtech.bot.core.domain.BoxAnswer;
import org.sadtech.bot.core.exception.MailSendException;
import org.sadtech.bot.core.service.sender.Sent;
import org.sadtech.vkbot.core.VkConnect;
import org.sadtech.vkbot.core.VkInsertData;

public class BoardCommentSenderVk implements Sent {

    private final VkApiClient vkApiClient;
    private final GroupActor groupActor;
    private final UserActor userActor;
    private final VkInsertData vkInsertData;

    public BoardCommentSenderVk(VkConnect vkConnect) {
        this.vkApiClient = vkConnect.getVkApiClient();
        this.groupActor = vkConnect.getGroupActor();
        this.vkInsertData = new VkInsertData(vkConnect);
        this.userActor = vkConnect.getUserActor();
    }

    @Override
    public void send(Integer integer, BoxAnswer boxAnswer) {
        throw new MailSendException();
    }

    @Override
    public void send(Integer contentId, Integer personId, BoxAnswer boxAnswer) {
        try {
            vkApiClient.board().createComment(userActor, groupActor.getGroupId(), contentId).message(vkInsertData.insertWords(boxAnswer.getMessage(), personId)).fromGroup(true).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }
}
