#!/usr/bin/env python

from treelib import Node, Tree

input = []
with open("day6.input") as file:
    input = [ line.strip() for line in file.readlines() if line.strip() != '' ]

# input.append('K)YOU')
# input.append('I)SAN')

def create_or_update(tree: Tree, name, parent=None):
    node = tree.get_node(name)
    if node is None:
        tree.create_node(name, name, parent=parent)
    else:
        tree.move_node(name, parent)

tree = Tree()
available_nodes = {}
tree.create_node("COM", "COM")
for line in input:
    names = line.split(')')
    for name in names:
        if name != "COM" and tree.get_node(name) is None:
            available_nodes[name] = 1
            tree.create_node(name, name, parent="COM")

for line in input:
    (left, right) = line.split(')')
    tree.move_node(right, left)

# print(tree)

orbits = 0
for name in available_nodes:
    node = tree.get_node(name)
    depth = tree.depth(node)
    # all_orbits_for_node = (depth * (depth + 1)) / 2
    all_orbits_for_node = depth
    # print(f"all orbits for {name}: {all_orbits_for_node}")
    orbits += all_orbits_for_node

print(orbits)

start_node = tree.parent("YOU").identifier
end_node = tree.parent("SAN").identifier

print(f"from {start_node} to {end_node}")

edge_nodes = [start_node]
already_seen = set([start_node])
found = False
steps = 0
while not found:
    new_edge = set()
    for edge_node in edge_nodes:
        children = [ n.identifier for n in tree.children(edge_node) if n is not None]
        parent_node = tree.parent(edge_node)
        if parent_node is not None:
            new_edge.add(parent_node.identifier)

        for n in children:
            new_edge.add(n)

    print(f"new edge: {new_edge} - already seen {already_seen}")

    new_edge.difference_update(already_seen)

    steps += 1

    if end_node in new_edge:
        print('done')
        found = True
        break

    already_seen.update(new_edge)

    edge_nodes = new_edge

    # print('enter', end='', flush=True)
    # import sys
    # sys.stdin.readline()

print(steps)
