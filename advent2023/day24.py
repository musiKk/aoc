#!/usr/bin/env python

from sympy import *

t1, t2, t3 = symbols('t1 t2 t3')
xs, ys, zs, dxs, dys, dzs = symbols('xs ys zs dxs dys dzs')

# x1, y1, z1, dx1, dy1, dz1 = 19, 13, 30, -2,  1, -2
# x2, y2, z2, dx2, dy2, dz2 = 18, 19, 22, -1, -1, -2
# x3, y3, z3, dx3, dy3, dz3 = 20, 25, 34, -2, -2, -4
x1, y1, z1, dx1, dy1, dz1 = 309254625334097, 251732589486275, 442061964691135, -42, -22, -45
x2, y2, z2, dx2, dy2, dz2 = 494902262649699, 448845738683125, 408766676225787, -345, -319, -201
x3, y3, z3, dx3, dy3, dz3 = 281199817421623, 235413393248399, 236652333766125, 89, 152, -70

eqns = [
    t1 * dxs + xs - t1 * dx1 - x1,
    t1 * dys + ys - t1 * dy1 - y1,
    t1 * dzs + zs - t1 * dz1 - z1,
    t2 * dxs + xs - t2 * dx2 - x2,
    t2 * dys + ys - t2 * dy2 - y2,
    t2 * dzs + zs - t2 * dz2 - z2,
    t3 * dxs + xs - t3 * dx3 - x3,
    t3 * dys + ys - t3 * dy3 - y3,
    t3 * dzs + zs - t3 * dz3 - z3,
]

result = solve_poly_system(eqns, [xs, ys, zs, dxs, dys, dzs, t1, t2, t3])
print(result)
