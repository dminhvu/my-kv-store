#!/bin/bash

# Configuration
PORT=6380
KEY="race_condition_test"
ITERATIONS=10
CONCURRENCY=20

echo "Resetting key..."
redis-cli -p $PORT SET $KEY 0

echo "Starting $CONCURRENCY workers performing $ITERATIONS increments each..."

for ((i=1; i<=$CONCURRENCY; i++)); do
    (
        for ((j=1; j<=$ITERATIONS; j++)); do
            # Get current value, increment it in bash, and set it back
            # This simulates a Read-Modify-Write race condition
            redis-cli -p $PORT INCR $KEY > /dev/null
        done
    ) &
done

wait
echo "Final value should be: $((CONCURRENCY * ITERATIONS))"
echo "Actual value in server: $(redis-cli -p $PORT GET $KEY)"