local key = KEYS[1]
local currentValue = redis.call('GET', key)

-- 如果键不存在，返回 -1
if not currentValue then
    return -1
end

-- 如果值为 "1"，更新为 "3"
if currentValue == "1" then
    -- 获取当前过期时间（毫秒）
    local ttl = redis.call('PTTL', key)
    -- 更新值并保留原始过期时间
    redis.call('SET', key, "3")
    -- 如果原有过期时间，重新设置相同TTL
    if ttl > 0 then
        redis.call('PEXPIRE', key, ttl)
    end
    return 1
end

-- 如果值为 "2"，直接返回 2
if currentValue == "2" then
    return 2
end

-- 如果值既不是 "1" 也不是 "2"，返回当前值（转换为数字）
return tonumber(currentValue) or -2