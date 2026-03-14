-- KEY[1] = key
-- ARGV[1] = UUID
-- ARGV[2] = now
-- ARGV[3] = windowStart

-- 1. Agregar el request al Sorted Set
redis.call("ZADD", KEYS[1], ARGV[2], ARGV[1])
-- 2. Eliminar los que quedaron fuera de la ventana
redis.call("ZREMRANGEBYSCORE", KEYS[1], 0, ARGV[3] - 1)
-- 3. Contar cuántos quedan
local value = redis.call("ZCARD", KEYS[1])
-- 4. Retornar el conteo
return value