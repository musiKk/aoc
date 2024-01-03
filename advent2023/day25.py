#!/usr/bin/env python


# found visually through the graph created with graphviz and tracing the
# connections in Inkscape
# example
# to_remove = [
#     ("bvb", "cmg"),
#     ("jqt", "nvd"),
#     ("hfx", "pzl")
# ]
to_remove = [
    ("gbc", "hxr"),
    ("tmt", "pnz"),
    ("mvv", "xkz")
]

def main():
    lines = []
    with open('input25.txt') as f:
        lines = [l.strip() for l in f.readlines()]

    edges = {}
    for line in lines:
        src, dsts = line.split(": ")
        for dst in dsts.split(" "):

            if src < dst:
                frm, to = src, dst
            else:
                frm, to = dst, src

            if frm not in edges:
                edges[frm] = []
            if to not in edges:
                edges[to] = []
            edges[frm].append(to)
            edges[to].append(frm)

    print(len(edges))

    for v1, v2 in to_remove:
        edges[v1] = [ v for v in edges[v1] if v != v2 ]
        edges[v2] = [ v for v in edges[v2] if v != v1 ]

    seen = set()
    queue = []
    queue.append(next(iter(edges.keys())))

    while queue:
        v = queue.pop()
        if v in seen:
            continue
        seen.add(v)
        for neighbor in edges[v]:
            queue.append(neighbor)
    print(len(seen))

if __name__ == '__main__':
    main()

# print("graph G {")
# for src, dsts in edges.items():
#     for dst in dsts:
#         print(f"    {src} -- {dst}")
# print("}")
