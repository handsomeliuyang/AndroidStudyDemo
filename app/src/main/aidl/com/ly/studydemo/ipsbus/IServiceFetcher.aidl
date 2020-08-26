package com.ly.studydemo.ipsbus;

interface IServiceFetcher {
    IBinder getService(String name);
}