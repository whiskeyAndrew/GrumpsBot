package com.example.TwitchBot.channelInfo;

//@Component
//public class ChannelModerators {
//    TwitchClient twitchClient;
//    Client client;
//    @Autowired
//    public void setTwitchClient(Client client) {
//        this.twitchClient = client.getTwitchClient();
//        this.client = client;
//    }
//
//
//
//    ModeratorList getModeratorsList() {
//
//        System.out.println("---------------MODERATORS---------------");
//        ModeratorList moderatorList = twitchClient.getHelix().getModerators(client.getChannelTokenAccess(), client.getChannelId(), null, null).execute();
//        moderatorList.getModerators().forEach(moderator -> {
//            System.out.println(moderator);
//        });
//        System.out.println();
//        return moderatorList;
//    }
//}
