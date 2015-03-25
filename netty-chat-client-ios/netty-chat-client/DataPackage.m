//
//  DataPackage.m
//  netty-chat-client
//
//  Created by Allen Zhong on 15/3/15.
//  Copyright (c) 2015年 Datafans Inc. All rights reserved.
//

#import "DataPackage.h"

static const NSUInteger TOATAL_SIZE_FIELD_LENGTH = 4;
static const NSUInteger VERSION_FIELD_LENGTH = 2;
static const NSUInteger MSGID_FIELD_LENGTH = 4;
static const NSUInteger TYPE_FIELD_LENGTH = 2;
static const NSUInteger COMMON_FIELD_LENGTH = 4;

static const NSUInteger HEADER_LENGTH = 16;

static const Byte VERSION_V1[2] = {0,1};
//static const Byte VERSION_V2[2] = {0,2};

static const Byte TYPE_HEARTBEAT[2] = {0,1};
static const Byte TYPE_LOGIN[2] = {0,2};
static const Byte TYPE_SIMPLE_MSG[3] = {0,3};

static DataPackage * _heartbeatPackage;
static DataPackage * _loginPackage;

@implementation DataPackage

- (instancetype)initWithVersion:(NSData *) version type:(NSData *) type
{
    self = [super init];
    if (self) {
        self.version = version;
        self.type = type;
    }
    return self;
}


- (instancetype)initWithVersion:(NSData *) version type:(NSData *) type content:(NSData *)content
{
    self = [self initWithVersion:version type:type];
    if (self) {
        self.content = content;
    }
    return self;
}




+(DataPackage *) heartbeatPackage
{
    if (_heartbeatPackage == nil) {
        NSData *versionData= [NSData dataWithBytes: VERSION_V1 length:VERSION_FIELD_LENGTH];
        NSData *typeData = [NSData dataWithBytes: TYPE_HEARTBEAT length:TYPE_FIELD_LENGTH];
        _heartbeatPackage = [[DataPackage alloc] initWithVersion:versionData type:typeData];
    }
    
    return  _heartbeatPackage;
}



+(DataPackage *) loginPackage
{
    if (_loginPackage == nil) {
        NSData *versionData= [NSData dataWithBytes: VERSION_V1 length:VERSION_FIELD_LENGTH];
        NSData *typeData = [NSData dataWithBytes: TYPE_LOGIN length:TYPE_FIELD_LENGTH];
        
        _loginPackage = [[DataPackage alloc] initWithVersion:versionData type:typeData];
    }
    
    return  _loginPackage;
}


+(DataPackage *) simpleMsgPackage
{
    
    NSData *versionData= [NSData dataWithBytes: VERSION_V1 length:VERSION_FIELD_LENGTH];
    NSData *typeData = [NSData dataWithBytes: TYPE_SIMPLE_MSG length:TYPE_FIELD_LENGTH];
    
    DataPackage *pkg = [[DataPackage alloc] initWithVersion:versionData type:typeData];
    
    return  pkg;
}


+(NSData *) encode:(DataPackage *) pkg
{
    NSMutableData *data = [NSMutableData data];
    
    //长度
    NSUInteger size = HEADER_LENGTH;
    if (pkg.content!=nil) {
        size += [pkg.content length];
    }
    Byte lenght[TOATAL_SIZE_FIELD_LENGTH] = {size >> 24, size >> 16, size >> 8, size};
    [data appendBytes:lenght length:TOATAL_SIZE_FIELD_LENGTH];
    
    //版本
    [data appendData:pkg.version];
    
    
    //消息id
    NSUInteger msgId = pkg.msgId;
    Byte msgIdByte[MSGID_FIELD_LENGTH] = {msgId >> 24, msgId >> 16, msgId >> 8, msgId};
    [data appendBytes:msgIdByte length:MSGID_FIELD_LENGTH];
    
    //类型
    [data appendData:pkg.type];
    
    //通用字段
    if (pkg.common == nil) {
        Byte common[COMMON_FIELD_LENGTH] = {0, 0, 0, 0};
        pkg.common = [NSData dataWithBytes:common length:COMMON_FIELD_LENGTH];
    }
    [data appendData:pkg.common];
    
    //数据
    if (pkg.content != nil) {
        [data appendData:pkg.content];
    }
    
    return data;
}



@end
