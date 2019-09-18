"""CS 61A Presents The Game of Hog."""

from dice import six_sided, four_sided, make_test_dice
from ucb import main, trace, interact

GOAL_SCORE = 100  # The goal of Hog is to score 100 points.

######################
# Phase 1: Simulator #
######################


def roll_dice(num_rolls, dice=six_sided):
    """Simulate rolling the DICE exactly NUM_ROLLS > 0 times. Return the sum of
    the outcomes unless any of the outcomes is 1. In that case, return 1.

    num_rolls:  The number of dice rolls that will be made.
    dice:       A function that simulates a single dice roll outcome.
    """
    # These assert statements ensure that num_rolls is a positive integer.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls > 0, 'Must roll at least once.'
    # BEGIN PROBLEM 1
    "*** YOUR CODE HERE ***"
    total = 0 #initialize the total
    have_one = False #a boolean value to check whether have outcome that is 1 or not
    while num_rolls > 0:#a while loop to go over
        current_roll = dice()
        if current_roll == 1:
            #if statement to change the have_one to true once 1 is founded
            have_one = True  # change to True, but we cant
            # use break statement because still need to keep rolling
        total += current_roll
        num_rolls -= 1#decrement of the condition
    if have_one:
        total = 1
    return total
    # END PROBLEM 1


def free_bacon(score):
    """Return the points scored from rolling 0 dice (Free Bacon).

    score:  The opponent's current score.
    """
    assert score < 100, 'The game should be over.'
    # BEGIN PROBLEM 2
    "*** YOUR CODE HERE ***"
    if score <= 10:#if 10 or lesser, least must be 0. 10 - 0 = 10
        return 10
    else:
        ones = score % 10
        tens = score // 10
        if ones >= tens:# if they are equal, just use either one
            return 10 - tens
        return 10 - ones
    # END PROBLEM 2


def take_turn(num_rolls, opponent_score, dice=six_sided):
    """Simulate a turn rolling NUM_ROLLS dice, which may be 0 (Free Bacon).
    Return the points scored for the turn by the current player.

    num_rolls:       The number of dice rolls that will be made.
    opponent_score:  The total score of the opponent.
    dice:            A function that simulates a single dice roll outcome.
    """
    # Leave these assert statements here; they help check for errors.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls >= 0, 'Cannot roll a negative number of dice in take_turn.'
    assert num_rolls <= 10, 'Cannot roll more than 10 dice.'
    assert opponent_score < 100, 'The game should be over.'
    # BEGIN PROBLEM 3
    "*** YOUR CODE HERE ***"
    if num_rolls == 0:#use if condition to check which kind of rolling should be used
        return free_bacon(opponent_score)
    else:
        return roll_dice(num_rolls, dice)
    # END PROBLEM 3


def is_swap(player_score, opponent_score):
    """
    Return whether the two scores should be swapped
    """
    # BEGIN PROBLEM 4
    "*** YOUR CODE HERE ***"
    right_most = opponent_score % 10
    left_most = player_score#initialize the left_most
    while left_most >= 10:#loop until reach the real left most
        left_most //= 10
    return right_most == left_most
    # END PROBLEM 4


def other(player):
    """Return the other player, for a player PLAYER numbered 0 or 1.

    >>> other(0)
    1
    >>> other(1)
    0
    """
    return 1 - player


def silence(score0, score1):
    """Announce nothing (see Phase 2)."""
    return silence


def play(strategy0, strategy1, score0=0, score1=0, dice=six_sided,
         goal=GOAL_SCORE, say=silence):
    """Simulate a game and return the final scores of both players, with Player
    0's score first, and Player 1's score second.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    strategy0:  The strategy function for Player 0, who plays first.
    strategy1:  The strategy function for Player 1, who plays second.
    score0:     Starting score for Player 0
    score1:     Starting score for Player 1
    dice:       A function of zero arguments that simulates a dice roll.
    goal:       The game ends and someone wins when this score is reached.
    say:        The commentary function to call at the end of the first turn.
    """
    player = 0  # Which player is about to take a turn, 0 (first) or 1 (second)
    # BEGIN PROBLEM 5
    "*** YOUR CODE HERE ***"
    while max(score0, score1) < goal:#loop until either one reaches the goal
        if player == 0:
            score0 += take_turn(strategy0(score0, score1), score1, dice)#update score
            if is_swap(score0, score1):#swap if needed
                score0, score1 = score1, score0
        else:
            score1 += take_turn(strategy1(score1, score0), score0, dice)
            if is_swap(score1, score0):
                score0, score1 = score1, score0
        player = other(player)#switch player

    # END PROBLEM 5
    # (note that the indentation for the problem 6 prompt (***YOUR CODE HERE***) might be misleading)
    # BEGIN PROBLEM 6
        say = say(score0, score1)#problem 6
    # END PROBLEM 6
    return score0, score1


#######################
# Phase 2: Commentary #
#######################


def say_scores(score0, score1):
    """A commentary function that announces the score for each player."""
    print("Player 0 now has", score0, "and Player 1 now has", score1)
    return say_scores

def announce_lead_changes(previous_leader=None):
    """Return a commentary function that announces lead changes.

    >>> f0 = announce_lead_changes()
    >>> f1 = f0(5, 0)
    Player 0 takes the lead by 5
    >>> f2 = f1(5, 12)
    Player 1 takes the lead by 7
    >>> f3 = f2(8, 12)
    >>> f4 = f3(8, 13)
    >>> f5 = f4(15, 13)
    Player 0 takes the lead by 2
    """
    def say(score0, score1):
        if score0 > score1:
            leader = 0
        elif score1 > score0:
            leader = 1
        else:
            leader = None
        if leader != None and leader != previous_leader:
            print('Player', leader, 'takes the lead by', abs(score0 - score1))
        return announce_lead_changes(leader)
    return say

def both(f, g):
    """Return a commentary function that says what f says, then what g says.

    NOTE: the following game is not possible under the rules, it's just
    an example for the sake of the doctest

    >>> h0 = both(say_scores, announce_lead_changes())
    >>> h1 = h0(10, 0)
    Player 0 now has 10 and Player 1 now has 0
    Player 0 takes the lead by 10
    >>> h2 = h1(10, 6)
    Player 0 now has 10 and Player 1 now has 6
    >>> h3 = h2(6, 17)
    Player 0 now has 6 and Player 1 now has 17
    Player 1 takes the lead by 11
    """
    def say(score0, score1):
        return both(f(score0, score1), g(score0, score1))
    return say


def announce_highest(who, previous_high=0, previous_score=0):
    """Return a commentary function that announces when WHO's score
    increases by more than ever before in the game.

    NOTE: the following game is not possible under the rules, it's just
    an example for the sake of the doctest

    >>> f0 = announce_highest(1) # Only announce Player 1 score gains
    >>> f1 = f0(12, 0)
    >>> f2 = f1(12, 11)
    11 point(s)! That's the biggest gain yet for Player 1
    >>> f3 = f2(20, 11)
    >>> f4 = f3(13, 20)
    >>> f5 = f4(20, 35)
    15 point(s)! That's the biggest gain yet for Player 1
    >>> f6 = f5(20, 47) # Player 1 gets 12 points; not enough for a new high
    >>> f7 = f6(21, 47)
    >>> f8 = f7(21, 77)
    30 point(s)! That's the biggest gain yet for Player 1
    >>> f9 = f8(77, 22) # Swap!
    >>> f10 = f9(33, 77) # Swap!
    55 point(s)! That's the biggest gain yet for Player 1
    """
    assert who == 0 or who == 1, 'The who argument should indicate a player.'
    # BEGIN PROBLEM 7
    def output_highest(score0, score1):
        if who == 0:
            score_use = score0
        else:
            score_use = score1
        difference = score_use - previous_score
        if difference > previous_high:#if is higher than previous highest, display that
            print(difference, 'point(s)! That\'s the biggest gain yet for Player', who)
            return announce_highest(who, difference, score_use)#return back to announce_highest function and update the previous highest and score
        return announce_highest(who, previous_high, score_use)#if not higher, do nothing but just return
    return output_highest#return the output_highest function we need to use

    # END PROBLEM 7


#######################
# Phase 3: Strategies #
#######################


def always_roll(n):
    """Return a strategy that always rolls N dice.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    >>> strategy = always_roll(5)
    >>> strategy(0, 0)
    5
    >>> strategy(99, 99)
    5
    """
    def strategy(score, opponent_score):
        return n
    return strategy


def make_averaged(fn, num_samples=1000):
    """Return a function that returns the average value of FN when called.

    To implement this function, you will have to use *args syntax, a new Python
    feature introduced in this project.  See the project description.

    >>> dice = make_test_dice(4, 2, 5, 1)
    >>> averaged_dice = make_averaged(dice, 1000)
    >>> averaged_dice()
    3.0
    """
    # BEGIN PROBLEM 8
    "*** YOUR CODE HERE ***"

    def function(*args):
        total = 0
        times = 0
        while times < num_samples:
            result = fn(*args)
            total += result
            times +=1
        return total / num_samples


    return function

    # END PROBLEM 8


def max_scoring_num_rolls(dice=six_sided, num_samples=1000):
    """Return the number of dice (1 to 10) that gives the highest average turn
    score by calling roll_dice with the provided DICE over NUM_SAMPLES times.
    Assume that the dice always return positive outcomes.

    >>> dice = make_test_dice(1, 6)
    >>> max_scoring_num_rolls(dice)
    1
    """
    # BEGIN PROBLEM 9
    "*** YOUR CODE HERE ***"
    highest_score = 0
    rolls = 1
    rolls_highest = 1
    current_average = make_averaged(roll_dice, num_samples)
    while rolls <= 10:
        current_score = current_average(rolls, dice)
        if current_score > highest_score:
            highest_score = current_score
            rolls_highest = rolls
        rolls += 1
    return rolls_highest


    # END PROBLEM 9


def winner(strategy0, strategy1):
    """Return 0 if strategy0 wins against strategy1, and 1 otherwise."""
    score0, score1 = play(strategy0, strategy1)
    if score0 > score1:
        return 0
    else:
        return 1


def average_win_rate(strategy, baseline=always_roll(4)):
    """Return the average win rate of STRATEGY against BASELINE. Averages the
    winrate when starting the game as player 0 and as player 1.
    """
    win_rate_as_player_0 = 1 - make_averaged(winner)(strategy, baseline)
    win_rate_as_player_1 = make_averaged(winner)(baseline, strategy)

    return (win_rate_as_player_0 + win_rate_as_player_1) / 2


def run_experiments():
    """Run a series of strategy experiments and report results."""
    if True:  # Change to False when done finding max_scoring_num_rolls
        six_sided_max = max_scoring_num_rolls(six_sided)
        print('Max scoring num rolls for six-sided dice:', six_sided_max)

    if True:  # Change to True to test always_roll(8)
        print('always_roll(6) win rate:', average_win_rate(always_roll(6)))

    if True:  # Change to True to test bacon_strategy
        print('bacon_strategy win rate:', average_win_rate(bacon_strategy))

    if True:  # Change to True to test swap_strategy
        print('swap_strategy win rate:', average_win_rate(swap_strategy))

    if True:  # Change to True to test final_strategy
        print('final_strategy win rate:', average_win_rate(final_strategy))

    "*** You may add additional experiments as you wish ***"


def bacon_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice if that gives at least MARGIN points, and
    rolls NUM_ROLLS otherwise.
    """
    # BEGIN PROBLEM 10
    if free_bacon(opponent_score) >= margin:
        return 0
    else:
        return num_rolls
    # END PROBLEM 10


def swap_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice when it triggers a beneficial swap. It also
    rolls 0 dice if it gives at least MARGIN points and does not trigger a
    non-beneficial swap. Otherwise, it rolls NUM_ROLLS.
    """
    # BEGIN PROBLEM 11
    #the whole discription of this part can be seperated to three:
    #1: if beneficial swap, return 0 no matter what
    #2: if not swap, and if least MARGIN, return 0
    #3:return num_rolls for else cases. including with not beneficial
    # or less than margin when not swap
    final_score = score + free_bacon(opponent_score)#try roll 0 dice
    if is_swap(final_score, opponent_score):#check if they can swap
        if opponent_score >= final_score:#whether beneficial or not
            return 0
        else:
            return num_rolls
    else:#when not swap, just following the bacon_strategy
        return bacon_strategy(score, opponent_score, margin, num_rolls)
    # END PROBLEM 11

def check_exp_score(dice = six_sided):
    """
    because if we run this fuction every time, then we have to run the make_averaged function every time
    which takes too much time.
    and actually each time the expected value will not change a lot. so we dont actually use this function.
    but instead, I just directly use the value I get from this function for the final Strategies
    """
    """print out the exp value of rolling diff number of dice
       here is the output:
       exp of rolling  1 dice is 3.4998
exp of rolling  1 dice is 3.51234
exp of rolling  2 dice is 5.83875
exp of rolling  3 dice is 7.37014
exp of rolling  4 dice is 8.20403
exp of rolling  5 dice is 8.62745
exp of rolling  6 dice is 8.6372
exp of rolling  7 dice is 8.53611
exp of rolling  8 dice is 8.17902
exp of rolling  9 dice is 7.76308
exp of rolling  10 dice is 7.33838

highest:   7 rolls. score is  8.6457
    """
    alist = []#alist that has 31 elements, each element is a list contains the #of rolling and the exp. the 31st element is the highest
    highest_score = 0
    rolls = 1
    rolls_highest = 1
    while rolls <= 10:#10 for 3000 times takes like around 3 to 4 seconds, which is good.
        current_average = make_averaged(roll_dice, 100000)
        current_score = current_average(rolls, dice)
        if current_score > highest_score:
            highest_score = current_score
            rolls_highest = rolls
        print("exp of rolling ", rolls, "dice is", current_score)
        sublist = [rolls, current_score]
        alist.append(sublist)
        rolls += 1
    highestlist = [rolls_highest, highest_score]
    print("highest: ", rolls_highest, "rolls. score is ", highest_score)
    return alist


def bubbleSort(arr):#to sort the exp score
    n = len(arr)

    # Traverse through all array elements
    for i in range(n):

        # Last i elements are already in place
        for j in range(0, n-i-1):

            # traverse the array from 0 to n-i-1
            # Swap if the element found is greater
            # than the next element
            if arr[j][1] < arr[j+1][1] :
                arr[j], arr[j+1] = arr[j+1], arr[j]



def final_strategy(score, opponent_score):
    """Write a brief description of your final strategy.

    #because the expected value of rolling different numbers of 6sided dice
    #is different. and because when u roll 1 the score is only one, so the
    #expected value has a limit and when the number of rolls increase the expected
    #value will start to decrease at a moment

    #so couple things we actually need to care in this strategy:
    #whether the exp score is higher than free_bacon or not, choose the one can give us higher score
    #but if causing negative swap, how much change between negative swap + exp and free_bacon
    #choose the higher one
    #the logic is choose the one can give us highest score at that turn(including with time trot)
    *** YOUR DESCRIPTION HERE ***
    """
    #exp_list = check_exp_score()#get the list of exp value of diff # of rolls
    exp_list = [[1, 3.51234], [2, 5.83875], [3, 7.37014], [4, 8.20403], [5, 8.62745], [6, 8.6372], [7, 8.53611], [8, 8.17902], [9, 7.76308], [10, 7.33838]]
    fb = free_bacon(opponent_score)#get the free bacon score
    exp_list.append([0, fb])#add the data of free bacon in the list
    #bubbleSort(exp_list)#sort it in order
    if is_swap(fb, opponent_score):
        fb = opponent_score - fb
    else:
        fb = fb - opponent_score

    for i in range(len(exp_list)):
        exp_list[i][1] = exp_list[i][1] + score
        if is_swap(exp_list[i][1], opponent_score):
            exp_list[i][1] = opponent_score - exp_list[i][1] - fb
        else:
            exp_list[i][1] = exp_list[i][1] - opponent_score - fb
    bubbleSort(exp_list)#sort it, the highest at the 00

    if exp_list[0][0] > -3.53611:#if not that bad, just use the highest. the reason why using this value is because the highest exp is 7 rolls get 8.53611 and exp of free bacon is 5.
        return exp_list[0][0]
    else:
        return 0

    #return swap_strategy(score, opponent_score, 9, 4)  # Replace this statement
    # END PROBLEM 12


##########################
# Command Line Interface #
##########################

# NOTE: Functions in this section do not need to be changed. They use features
# of Python not yet covered in the course.


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions.

    This function uses Python syntax/techniques not yet covered in this course.
    """
    import argparse
    parser = argparse.ArgumentParser(description="Play Hog")
    parser.add_argument('--run_experiments', '-r', action='store_true',
                        help='Runs strategy experiments')

    args = parser.parse_args()

    if args.run_experiments:
        run_experiments()
