# Lucas Woodbury
# COSC 4550
# Final Project
# 12/12/16


#!/usr/bin/env python
# python_example.py
# Author: Ben Goodrich
#
# This is a direct port to python of the shared library example from
# ALE provided in doc/examples/sharedLibraryInterfaceExample.cpp
import sys
from random import randrange
from ale_python_interface import ALEInterface
import util
import numpy as np

background = np.array([0,0,0],dtype=np.uint8)
player_white = np.array([214,214,214],dtype=np.uint8)
player_blue = np.array([66,72,200],dtype=np.uint8)
projectile_white = np.array([236, 236, 236], dtype=np.uint8)
enemy_projectile = np.array([187, 187, 53], dtype=np.uint8)


def screenData(ale):
  screen = ale.getScreenRGB() # row major
  screen_dims = ale.getScreenDims()
  color_pixels = []
  
  for i in range(screen_dims[1]): # rows
    for j in range(screen_dims[0]): # columns 
      if not np.array_equal(screen[i][j], background):
        print "location: ", (i,j), "color: ", screen[i][j]   
  ale.saveScreenPNG("screenshot.png")

# dumbest heuristic ever
def dummyheuristic(ale,succ_reward):
  return succ_reward

# score: [195 144 61]
# mothership: [180 122 48], [187 187 53], [24 59 157], [110 156  66],
#             [72 160 72], [51 26 163], [236 236 236]
# enemy: [167 26 26], [181 83 40], [180 122 48]
#      ranges: rows 53-60, 78-85, 103-110, 178-185
# enemy projectile: [187 187 53]
# player: [66 72 200], [214 214 214], [162 162 42]
#      range: rows 178-185
# player projectile: [236 236 236]
# baseline: [66 158 130]
# lives: [170 170 170]
#      range: rows 192-199, columns 15-22 31-38 47-54

# featureLocations() takes the ale interface and returns a list of pairs consisting of
# the x-location and the width from one side to the center(the "radius") of the player each enemy 
def featureLocations(ale):
  screen = ale.getScreenRGB() # row major
  locations = []
  row1min =  row2min =  row3min =  row4min_enemy = row4min_player = 160
  row1max = row2max = row3max = row4max_enemy = row4max_player = -1

  for j in range(159):
    if np.array_equal(screen[178][j],player_blue):
      if j < row4min_player:
        row4min_player = j
      if j > row4max_player:
        row4max_player = j
  if row4min_player != 160 and row4max_player != -1:
    locations = locations + [(row4min_player, 4)]
  
  # search each enemy band


  for i in range(53,61): # x-values for row 1
    for j in range(159):
      if not np.array_equal(screen[i][j], background):
        if j < row1min:
          row1min = j
        if j > row1max:
          row1max = j
  if row1min != 160 and row1max != -1:
    w = (row1max-row1min)/2
    locations = locations + [(row1min + w, w)]

  for i in range(78,86): # x-values for row 2
    for j in range(159):
      if not np.array_equal(screen[i][j], background):
        if j < row2min:
          row2min = j
        if j > row2max:
          row2max = j
  if row2min != 160 and row2max != -1:
    w = (row2max-row2min)/2
    locations = locations + [(row2min + w, w)]

  for i in range(103,111): # x-values for row 3
    for j in range(159):
      if not np.array_equal(screen[i][j], background):
        if j < row3min:
          row3min = j
        if j > row3max:
          row3max = j
  if row3min != 160 and row3max != -1:
    w = (row3max-row3min)/2
    locations = locations + [(row3min + w, w)]

  for i in range(178,186): # x-values for player and enemy in row 4
    for j in range(159):
      if not np.array_equal(screen[i][j], player_white) and \
         not np.array_equal(screen[i][j], background) and \
         not np.array_equal(screen[i][j],projectile_white) and \
         not np.array_equal(screen[i][j],enemy_projectile): # not player, not background, not projectile, enemy pixel
        if j < row4min_enemy:
          row4min_enemy = j
        if j > row4max_enemy:
          row4max_enemy = j
  if row4min_enemy != 160 and row4max_enemy != -1:
    w = (row4max_enemy-row4min_enemy)/2
    locations = locations + [(row4min_enemy + w, w)]
    
  return locations 

def enemyCount(ale):
  screen = ale.getScreenRGB() # row major
  enemy_count = 0
  # search a single row through each enemy band
  for i in range(159): # x-values for row 1
    if not np.array_equal(screen[56][i], background) and \
       not np.array_equal(screen[56][i], projectile_white) and \
       not np.array_equal(screen[56][i],enemy_projectile):
      enemy_count += 1
      break
  for i in range(159): # x-values for row 2
    if not np.array_equal(screen[81][i], background) and \
       not np.array_equal(screen[81][i], projectile_white) and \
       not np.array_equal(screen[81][i],enemy_projectile):
      enemy_count += 1
      break
  for i in range(159): # x-values for row 3
    if not np.array_equal(screen[106][i], background) and \
       not np.array_equal(screen[106][i], projectile_white) and \
       not np.array_equal(screen[106][i],enemy_projectile):
      enemy_count += 1
      break
  for i in range(159): # x-values for all rows
    if not np.array_equal(screen[181][i], background) and \
       not np.array_equal(screen[181][i], player_white) and \
       not np.array_equal(screen[181][i],enemy_projectile):
      enemy_count += 1
      break
  return enemy_count

def lifeCount(ale):
  screen = ale.getScreenRGB()
  lives = 0
  if not np.array_equal(screen[195][18], background):
    lives += 1
  if not np.array_equal(screen[195][34], background):
    lives += 1
  if not np.array_equal(screen[195][50], background):
    lives += 1
  return lives

def gunHeat(ale):
  screen = ale.getScreenRGB()
  maximum = -1
  minimum = 160
  for i in range(80,159):
    if not np.array_equal(screen[195][i],background):
      if i > maximum:
        maximum = i
      if i <  minimum:
        minimum = i
  return (maximum - minimum)
      

def playerCount(ale):
  screen = ale.getScreenRGB()
  for j in range(159):
    if np.array_equal(screen[178][j],player_blue):
      return 1
  return 0

def killTest(ale,node):
  ale.restoreState(node)
  numEnemies = enemyCount(ale)
  legal_actions = ale.getMinimalActionSet()
  for i in range(10):
    ale.act(legal_actions[1])
    if enemyCount(ale) < numEnemies:
      return True
  return False  

def deathTest(ale):
  screen = ale.getScreenRGB()
  for i in range(3):
    ale.act(legal_actions[1]) 
    if not np.array_equal(screen[2][2],background):
      return True
  return False

def heuristic(ale,node,caction,numLives):
  ale.restoreState(node)
  locs = featureLocations(ale)
  furthestdist = min([abs(locs[x][0]-locs[0][0]) for x in range(1,len(locs))])/2
  g =  0.7 # max of gunheat is about 70
  if gunHeat(ale) > 40:
    g = 0.99
  lives = 0.7
  if deathTest(ale):
    lives = 0.99
  enemycount = len(locs) - 1
  h = (enemycount + furthestdist) * lives * g
  return h

def wait(ale):
  reward = 0
  for i in range(2):
    reward += ale.act(legal_actions[1])
  return reward

def aStar(ale,heurisitic):
  legal_actions = ale.getMinimalActionSet()
  node = ale.cloneState()
  frontier = util.PriorityQueue()
  frontier.push((node,[],0,0),0)
  frontierlist = dict()
  visited = []
  lastnode = None
  lastNumEnemies = enemyCount(ale)
##  thisNumEnemies = 0
  numLives = 0
  ale.setBool('display_screen', False)

  # if no enemies, don't do the search
  if lastNumEnemies == 0:
    return []

  # if no player, don't search
  if playerCount(ale) == 0:
    return []
  
  while True:
    if frontier.isEmpty(): 
      return []
    # pop a node from the priority queue
    node,actions,cost,level = frontier.pop() 
    ale.restoreState(node)
    
    # find number of lives for this node
    numLives = lifeCount(ale)
    
##    # test if a new enemy appeared, if so, return and start new search
##    thisNumEnemies = enemyCount(ale)
##    if  thisNumEnemies > lastNumEnemies:
##      return actions
##    else:
##      lastNumEnemies = thisNumEnemies
    # level limiter
    if level >= 3:
      if len(actions) <= 0:
        return [legal_actions[1]]
      return actions
    # if game ends, don't expand it, dont return either
    if ale.game_over():
      return actions
    # remove from frontier list
    if node in frontierlist:
      del frontierlist[node]
    # test if node has been visited, if so
    if node in visited:
      continue
    visited = visited + [node]
    
    # node passed all tests, will now be expanded
    children = []
    for i in range(1,7):
      clevel = level + 1
      creward = wait(ale)
      creward += ale.act(legal_actions[i])
      child = ale.cloneState()
      if not ale.game_over():
        children = children + [(child,legal_actions[i],clevel)]
      ale.restoreState(node)
      
    for c in children:
        child,caction,clevel = c
        newcost = cost + 2
        # test for kill, if kill detected, set priority arbitrarily high (-1)
##        if caction == legal_actions[2] or \
##           caction == legal_actions[5] or \
##           caction == legal_actions[6]:
##          if killTest(ale,child):
##            print "killTest is true"
##            priority = (newcost + heuristic(ale,child,caction,numLives)) * 0.2
##          else:
##            priority = newcost + heuristic(ale,child,caction,numLives)
##        else:
        priority = newcost + heuristic(ale,child,caction,numLives)
        ale.restoreState(node)
        waitlist = [legal_actions[1]]
        new_actions = actions + (waitlist + [caction])
        # if necessary, set best reward
        if child not in visited and child not in frontierlist:
            frontier.push( ( child , new_actions, newcost,clevel ), priority)
            frontierlist[child] = newcost
        elif child in frontierlist and newcost < frontierlist[child]:
            frontier.push( ( child , new_actions, newcost,clevel ), priority)
            frontierlist[child] = newcost


if len(sys.argv) < 2:
  print('Usage: %s rom_file' % sys.argv[0])
  sys.exit()

ale = ALEInterface()

# Get & Set the desired settings
ale.setInt(b'random_seed', 123)

# Set USE_SDL to true to display the screen. ALE must be compilied
# with SDL enabled for this to work. On OSX, pygame init is used to
# proxy-call SDL_main.
USE_SDL = True
if USE_SDL:
  if sys.platform == 'darwin':
    import pygame
    pygame.init()
    ale.setBool('sound', False) # Sound doesn't work on OSX
  elif sys.platform.startswith('linux'):
    ale.setBool('sound', False)
  ale.setBool('display_screen', True)

# Load the ROM file
rom_file = str.encode(sys.argv[1])
ale.loadROM(rom_file)

# Get the list of legal actions
legal_actions = ale.getMinimalActionSet() # length = 7
# do nothing: [0,1]
# shoot up: [2]
# move right: [3]
# move left: [4]
# shoot right: [5]
# shoot left: [6]
# Play 10 episodes

# a list to record the path taken
action_list = []
for episode in range(1):
  total_reward = 0
  actions = []
  while not ale.game_over():
    action_list = action_list + [legal_actions[1],legal_actions[1]]
    for i in range(2):
      total_reward += ale.act(legal_actions[1])
    if actions == []:
      actions = aStar(ale,heuristic)
      if actions == []:
        actions = [legal_actions[0]]
    act = actions.pop(0)
    action_list = action_list + [act]
    total_reward += ale.act(act)

  print('Episode %d ended with score: %d' % (episode, total_reward))
  ale.saveScreenPNG("p_s_w_h.png")
ale.reset_game()

    
