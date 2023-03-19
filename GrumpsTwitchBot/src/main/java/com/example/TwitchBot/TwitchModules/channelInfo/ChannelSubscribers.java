package com.example.TwitchBot.TwitchModules.channelInfo;

//@Component
//public class ChannelSubscribers {
//    TwitchClient twitchClient;
//    TwitchClientConfig client;
//    @Autowired
//    public void setTwitchClient(TwitchClientConfig client) {
//        this.twitchClient = client.getTwitchClient();
//        this.client = client;
//    }
//
//
//    SubscriptionList getSubscriptionList(){
//        System.out.println("---------------SUBSCRIBERS---------------");
//        SubscriptionList resultList = twitchClient.getHelix().getSubscriptions( client.getChannelName(), client.getChannelId(), null, null,null).execute();
//        resultList.getSubscriptions().forEach(subscription -> {
//            System.out.println("Subscriber: "+subscription.getUserName());
//        });
//        System.out.println();
//        return  resultList;
//    }
//}
