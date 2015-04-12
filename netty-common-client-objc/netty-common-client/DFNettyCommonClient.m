//
//  DFNettyChatClient.m
//  netty-chat-client
//
//  Created by Allen Zhong on 15/3/15.
//  Copyright (c) 2015年 Datafans Inc. All rights reserved.
//

#import "DFNettyCommonClient.h"
#import "Reachability.h"


#define TAG_HEADER 1001
#define TAG_BODY 1002

@implementation DFNettyCommonClient



- (instancetype)init
{
    self = [super init];
    if (self) {
        
        _reConnectTimes = 0;
        [self setStatus:CLOSED];
        _hasMarkedRead = NO;
        
        _socket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:dispatch_queue_create("VNCSocketQueue", NULL)];
    }
    return self;
}


-(void) connect:(NSString *) host port:(NSInteger) port
{
    _host = host;
    _port = port;
    [self start];
}


-(void) start
{
    if (_status != CLOSED) {
        return;
    }
    
    _hasMarkedRead = NO;
    
    NSError *error = nil;
    [_socket connectToHost:_host onPort:_port error:&error];
    
    [self setStatus:CONNECTING];
    
    if (error != nil) {
        NSLog(@"%@",error);
        [self setStatus:CLOSED];
    }
    
    [self startTimer];
}


-(void) close
{
    [_socket disconnect];
    
}
-(void) disconnect
{
    [self close];
    
    [_autoRestartTimer invalidate];
    _autoRestartTimer = nil;
    [_autoSendHeartbeatTimer invalidate];
    _autoSendHeartbeatTimer = nil;
    [_heartbeatTimeoutCheckTimer invalidate];
    _heartbeatTimeoutCheckTimer = nil;
}



-(void) setStatus:(CONNECTION_STATUS)status
{
    _status = status;
    dispatch_async(dispatch_get_main_queue(), ^{
        
        NSDictionary *dic = [NSDictionary dictionaryWithObject:[NSNumber numberWithInt:_status] forKey:@"status"];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"STATUS" object:nil userInfo:dic];
        
    });
}

-(void) startTimer
{
    //自动重连
    if (_autoRestartTimer == nil) {
        _autoRestartTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(autoRestart:) userInfo:nil repeats:YES];
    }
    
    
    //发送心跳
    if (_autoSendHeartbeatTimer == nil) {
        _autoSendHeartbeatTimer = [NSTimer scheduledTimerWithTimeInterval:HEARTBEAT_SEND_INTERVAL target:self selector:@selector(autoSendHeartbeat:) userInfo:nil repeats:YES];
    }
    
    
    //心跳超时检测及网络检测
    if (_heartbeatTimeoutCheckTimer == nil) {
        _heartbeatTimeoutCheckTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(heartbeatTimeoutCheck:) userInfo:nil repeats:YES];
    }
    
    
}



-(void) write:(NSData *) data
{
    [self write:data tag:1];
}

-(void) write:(NSData *) data tag:(NSUInteger) tag
{
    [_socket writeData: data withTimeout:-1 tag:tag];
}

- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port{
    
    NSLog(@"socket_connected! %@", sock);
    
    [self setStatus:RUNNING];
    _lastActiveTime = [self now];
    
    [self login];
    
}

-(void) login
{
    NSLog(@"login");
    
    [self write:[self getLoginData]];
}
-(void)socketDidCloseReadStream:(GCDAsyncSocket *)sock
{
    NSLog(@"socket_close_read_stream!");
}

-(void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err
{
    NSLog(@"socket_disconnected!");
    [self setStatus:CLOSED];
}


-(void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    NSLog(@"socket_write_with_tag: %ld",tag);
    //[_socket readDataWithTimeout:-1 tag:1];
    if (!_hasMarkedRead) {
        [_socket readDataToLength:4 withTimeout:-1 tag:TAG_HEADER];
    }
    
}


-(void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
    NSLog(@"socket_read_with_tag: %ld   data: %@",tag,data);
    _lastActiveTime = [self now];
    
    _hasMarkedRead = NO;
    
    if (tag == TAG_HEADER)
    {
        NSLog(@"header: %@",data);
        int bodyLength= [self dataToInt:data] - 4 ;
        [_socket readDataToLength:bodyLength withTimeout:-1 tag:TAG_BODY];
    }
    else if (tag == TAG_BODY)
    {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [[NSNotificationCenter defaultCenter] postNotificationName:@"kkk" object:nil userInfo:nil];
        });
        [_socket readDataToLength:4 withTimeout:-1 tag:TAG_HEADER];
    }
    _hasMarkedRead = YES;
    
}

-(int) dataToInt:(NSData *) data
{
    unsigned char bytes[4];
    [data getBytes:bytes length:4];
    uint32_t n = (int)bytes[0] << 24;
    n |= (int)bytes[1] << 16;
    n |= (int)bytes[2] << 8;
    n |= (int)bytes[3];
    
    return n;
}

-(void) autoRestart:(NSTimer *) timer
{
    
    
    if (_status != CLOSED) {
        return;
    }
    _reConnectTimes++;
    
    NSLog(@"reconnect_times: %ld",(long)_reConnectTimes);
    
    [self start];
    
    
}


-(void) autoSendHeartbeat:(NSTimer *) timer
{
    
    
    if (_status != RUNNING) {
        return;
    }
    
    NSLog(@"send_heartbeat");
    
    [self write:[self getHeartbeatData]];
    
}

-(void) heartbeatTimeoutCheck:(NSTimer *) timer
{
    if (![self isNetworkAvailable]) {
        [self close];
    }
    
    NSTimeInterval now = [self now];
    if (now-_lastActiveTime > HEARTBEAT_TIMEOUT_INTERVAL) {
        NSLog(@"heartbeat_timeout");
        [self close];
    }
}

-(BOOL) isNetworkAvailable
{
    return [Reachability isEnable3G] || [Reachability isEnableWIFI]?YES:NO;
}


-(NSData *) getHeartbeatData
{
    return nil;
}

-(NSData *) getLoginData
{
    
    return nil;
    
}


-(NSTimeInterval) now
{
    return [[NSDate date] timeIntervalSince1970];
}


@end
