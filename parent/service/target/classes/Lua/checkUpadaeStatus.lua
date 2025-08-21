local key = KEYS[1]
local currentValue = redis.call('GET', key)

if not currentValue then
    return -1
end

if currentValue == "1" then
    local ttl = redis.call('PTTL', key)
    redis.call('SET', key, "2")
    if ttl > 0 then
        redis.call('PEXPIRE', key, ttl)
    end
    return 1
end

return tonumber(currentValue) or -2