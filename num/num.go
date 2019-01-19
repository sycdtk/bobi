package num

//返回正整数，在原有正整数最大值的基础上+1，若未负数则返回1
func NewMax(nums []int) int {
	result := 0
	if len(nums) > 0 {
		for _, num := range nums {
			if result < num {
				result = num
			}
		}

	}
	return result + 1
}
