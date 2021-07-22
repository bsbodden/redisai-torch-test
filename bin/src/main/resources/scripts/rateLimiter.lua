-- rateLimiter.lua
local key = KEYS[1]
local requests = tonumber(redis.call('GET', key) or '-1')
local max_requests = tonumber(ARGV[1])
local expiry = tonumber(ARGV[2])

if (requests == -1) then
  redis.call('SET', key, 1)
elseif (requests < max_requests) then
  redis.call('INCR', key)
  redis.call('EXPIRE', key, expiry)
  return false
else
  return true
end