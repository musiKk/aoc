#include<stdio.h>
#include<string.h>
#include<stdlib.h>

struct node {
    int value;
    struct node *next;
};

int CUP_COUNT = 9;

char* input = "389125467";


void dump_cups(struct node *cups) {
    struct node *cup = cups;
    int first = 1;
    int c = 0;
    while (cup != cups || first == 1) {
        c++;
        first = 0;
        printf("%d, ", cup->value);
        cup = cup->next;
    }
    // printf("count: %d\n", c);
}

void init_cups(int cup_count, struct node cups[]) {
    for (int i=0; i<cup_count; i++) {
        cups[i].value = i + 1;
    }
}

void remove_cups(struct node *cup_before, struct node **removed_cups) {
    *removed_cups = cup_before->next;
    cup_before->next = cup_before->next->next->next->next;
}

void find_destination_cup(
    struct node *cups,
    struct node *current_cup,
    struct node *removed_cups,
    struct node **destination_cup) {
    int removed_cup_values[3] = {
        removed_cups->value,
        removed_cups->next->value,
        removed_cups->next->next->value
    };
    int destination_cup_candidate_value = current_cup->value;
    do {
        destination_cup_candidate_value--;
        if (destination_cup_candidate_value < 1) {
            destination_cup_candidate_value = CUP_COUNT;
        }
    } while (removed_cup_values[0] == destination_cup_candidate_value
            || removed_cup_values[1] == destination_cup_candidate_value
            || removed_cup_values[2] == destination_cup_candidate_value);
    *destination_cup = &cups[destination_cup_candidate_value - 1];
}

void insert_removed_cups(struct node *removed_cups, struct node *destination_cup) {
    struct node *current_next = destination_cup->next;
    destination_cup->next = removed_cups;
    removed_cups->next->next->next = current_next;
}

int main(int argc, char** args) {
    // int NODE_COUNT = 9;
    int NODE_COUNT = 1000000;
    struct node *cups = malloc(NODE_COUNT*sizeof(struct node));
    init_cups(NODE_COUNT, cups);

    struct node *current_node = &cups[input[0] - '0' - 1];
    struct node *first = current_node;

    for (int i=1; i<strlen(input); i++) {
        int value = input[i] - '0';
        struct node *new_node = &cups[value - 1];
        current_node->next = new_node;
        current_node = new_node;
    }

    for (int i=strlen(input) + 1; i<=NODE_COUNT; i++) {
        // printf("extending original list with %d\n", i);
        struct node *new_node = &cups[i - 1];
        current_node->next = new_node;
        current_node = new_node;
    }

    current_node->next = first;
    // dump_cups(first);
    // exit(0);

    struct node *current_cup = first;
    for (int i = 0; i < 10000000; i++) {
        if ((i + 1) % 1000000 == 0) {
            printf("-- move %d --\n", i + 1);
        }
        // dump_cups(current_cup);
        // printf("\n");
        struct node *removed_cups, *destination_cup;
        remove_cups(current_cup, &removed_cups);
        // printf("removed: %d, %d, %d\n", removed_cups->value, removed_cups->next->value, removed_cups->next->next->value);
        find_destination_cup(cups, current_cup, removed_cups, &destination_cup);
        // printf("destination %d\n\n", destination_cup->value);
        insert_removed_cups(removed_cups, destination_cup);
        current_cup = current_cup->next;
    }

    // dump_cups(current_cup);

    current_node = current_cup;
    while (1) {
        if (current_node->value == 1) {
            printf("values: %d, %d\n",
                    current_node->next->value,
                    current_node->next->next->value);
            break;
        }
        current_node = current_node->next;
    }

}
