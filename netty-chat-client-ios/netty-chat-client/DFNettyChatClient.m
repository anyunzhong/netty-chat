//
//  DFNettyChatClient.m
//  netty-chat-client
//
//  Created by Allen Zhong on 15/3/18.
//  Copyright (c) 2015年 Datafans Inc. All rights reserved.
//

#import "DFNettyChatClient.h"
#import "DataPackage.h"

@implementation DFNettyChatClient

static DFNettyChatClient *_client=nil;

+(instancetype) sharedInstance
{
    @synchronized(self){
        if (_client == nil) {
            _client = [[DFNettyChatClient alloc] init];
        }
    }
    return _client;
}




-(NSData *) getHeartbeatData
{
    DataPackage *pkg = [DataPackage heartbeatPackage];
    return [DataPackage encode:pkg];
}

-(NSData *) getLoginData
{
    DataPackage *pkg = [DataPackage loginPackage];
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    [dic setObject:@"10000" forKey:@"user_id"];
    NSData *content = [[self dataTOjsonString:dic] dataUsingEncoding:NSUTF8StringEncoding];
    pkg.content = content;
    
    return [DataPackage encode:pkg];

    
}


-(NSString*)dataTOjsonString:(id)object
{
    NSString *jsonString = nil;
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:object
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
    if (! jsonData) {
        NSLog(@"Got an error: %@", error);
    } else {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    return jsonString;
}


@end
