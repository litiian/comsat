/*
 * COMSAT
 * Copyright (c) 2016, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.fibers.redis;

import co.paralleluniverse.fibers.Suspendable;
import com.lambdaworks.redis.pubsub.StatefulRedisPubSubConnection;
import com.lambdaworks.redis.pubsub.api.async.RedisPubSubAsyncCommands;
import io.netty.util.internal.EmptyArrays;
import redis.clients.jedis.Client;

import java.util.concurrent.atomic.AtomicLong;

import static co.paralleluniverse.fibers.redis.Utils.checkPubSubConnected;

/**
 * @author circlespainter
 */
@SuppressWarnings("WeakerAccess")
public class BinaryJedisPubSub extends redis.clients.jedis.BinaryJedisPubSub {
    BinaryJedis jedis;
    StatefulRedisPubSubConnection<byte[], byte[]> conn;
    RedisPubSubAsyncCommands<byte[], byte[]> commands;

    AtomicLong subscribedChannels = new AtomicLong();

    @Override
    @Suspendable
    public final void unsubscribe() {
        checkPubSubConnected(jedis, conn, commands);
        jedis.await(() -> commands.unsubscribe());
    }

    @Override
    @Suspendable
    public final void punsubscribe() {
        checkPubSubConnected(jedis, conn, commands);
        jedis.await(() -> commands.punsubscribe());
    }

    @Override
    @Suspendable
    public final void unsubscribe(byte[]... channels) {
        checkPubSubConnected(jedis, conn, commands);
        jedis.await(() -> commands.unsubscribe(channels));
    }

    @Override
    @Suspendable
    public final void punsubscribe(byte[]... patterns) {
        checkPubSubConnected(jedis, conn, commands);
        jedis.await(() -> commands.punsubscribe(patterns));
    }

    @Override
    @Suspendable
    public final void subscribe(byte[]... channels) {
        checkPubSubConnected(jedis, conn, commands);
        jedis.await(() -> commands.subscribe(channels));
    }

    @Override
    @Suspendable
    public final void psubscribe(byte[]... patterns) {
        checkPubSubConnected(jedis, conn, commands);
        jedis.await(() -> commands.psubscribe(patterns));
    }

    @Override
    public final boolean isSubscribed() {
        return getSubscribedChannels() > 0;
    }

    @Override
    public final int getSubscribedChannels() {
        return Utils.validateInt(subscribedChannels.get());
    }

    @Override
    public final void proceedWithPatterns(Client client, byte[]... patterns) {
        // Nothing to do
    }

    @Override
    public final void proceed(Client client, byte[]... channels) {
        // Nothing to do
    }

    final void close() {
        commands = null;
        conn.close();
        conn = null;
        jedis = null;
    }
}