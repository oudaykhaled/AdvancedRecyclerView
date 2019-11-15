package com.appro.stickyheadercursorrecycleradapter.adapter


/**
 * Created by Ouday Khaled on 2/20/2018.
 */

class ChatModel {

    var message = ""
    var chatID = ""
    var dateOfCreation: String? = null

    companion object {

        fun createChatModel(body: String, id: String, jid: String): ChatModel {
            val chatModel = ChatModel()
            chatModel.message = body
            chatModel.chatID = id
            chatModel.dateOfCreation = System.currentTimeMillis().toString() + ""
            return chatModel
        }
    }

}
