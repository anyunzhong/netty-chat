//
//  DataPackage.h
//  netty-chat-client
//
//  Created by Allen Zhong on 15/3/15.
//  Copyright (c) 2015年 Datafans Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface DataPackage : NSObject

@property (nonatomic,assign) NSInteger size;
@property (nonatomic,strong) NSData  *version;
@property (nonatomic,assign) NSInteger msgId;
@property (nonatomic,strong) NSData *type;
@property (nonatomic,strong) NSData *common;
@property (nonatomic,strong) NSData *content;


+(DataPackage *) heartbeatPackage;
+(DataPackage *) loginPackage;
+(DataPackage *) simpleMsgPackage;

+(NSData *) encode:(DataPackage *) pkg;

- (instancetype)initWithVersion:(NSData *) version type:(NSData *) type;
- (instancetype)initWithVersion:(NSData *) version type:(NSData *) type content:(NSData *)content;
@end
