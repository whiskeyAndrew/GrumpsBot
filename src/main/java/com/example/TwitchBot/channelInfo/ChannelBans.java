package com.example.TwitchBot.channelInfo;

//@Component
//public class ChannelBans {
//    TwitchClient twitchClient;
//    Client client;
//    @Autowired
//    public void setTwitchClient(Client client) {
//        this.twitchClient = client.getTwitchClient();
//    }
//        BannedUserList getBannedList() {
//
//        System.out.println("---------------BANS---------------");
//        BannedUserList bannedUserList = twitchClient.getHelix().getBannedUsers(client.getChannelTokenAccess(), client.getChannelId(), null, null, null).execute();
//        bannedUserList.getResults().forEach(bannedUser -> {
//            System.out.println(bannedUser);
//        });
//        System.out.println();
//        return bannedUserList;
//    }
//}
