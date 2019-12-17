#!/usr/bin/env python

input = ''
with open('day8.input') as file:
    input = file.readline().strip()

layers = []
layer_idx = 0
while True:
    try:
        cur_layer = []
        for y in range(0, 6):
            cur_layer.append([])
            for x in range(0, 25):
                cur_pixel = int(input[y * 25 + x + layer_idx * (25*6)])
                cur_layer[-1].append(cur_pixel)
        layer_idx += 1
    except:
        break
    layers.append(cur_layer)

layer_metas = []
for layer in layers:
    layer_meta = { 'zero': 0, 'one': 0, 'two': 0 }
    layer_metas.append(layer_meta)
    for row in layer:
        for pixel in row:
            if pixel == 0:
                layer_meta['zero'] += 1
            elif pixel == 1:
                layer_meta['one'] += 1
            elif pixel == 2:
                layer_meta['two'] += 1

# print(layer_metas)
[ print(layer_meta) for layer_meta in layer_metas ]

final_image = [ [0] * 25 for y in range(0, 6) ]

for layer in reversed(layers):
    for y in range(0, 6):
        for x in range(0, 25):
            px = layer[y][x]
            if px == 2:
                pass
            elif px == 1:
                final_image[y][x] = 1
            elif px == 0:
                final_image[y][x] = 0

print(final_image)
for row in final_image:
    for col in row:
        if col == 0:
            print(' ', end='')
        elif col == 1:
            print('#', end='')
    print('')
